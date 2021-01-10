package me.parser.ikvf

import cats.implicits._
import cats.Monad
import me.parser.ikvf.AST._
import me.parser.ikvf.decoder.Decoder

object implicits {

  implicit val decoderMonad: Monad[Decoder] = new Monad[Decoder] {

    def flatMap[A, B](fa: Decoder[A])(f: A => Decoder[B]): Decoder[B] =
      (obj: IKVFValue) =>
        for {
          a <- fa.decode(obj)
          b <- f(a).decode(obj)
        } yield b

    // fixme: this is not tailrec ;(
    def tailRecM[A, B](a: A)(f: A => Decoder[Either[A, B]]): Decoder[B] = { obj =>
      f(a)
        .decode(obj)
        .flatMap {
          _.leftFlatMap(tailRecM(_)(f).decode(obj))
        }
    }

    def pure[A](x: A): Decoder[A] = _ => x.asRight

  }

}
