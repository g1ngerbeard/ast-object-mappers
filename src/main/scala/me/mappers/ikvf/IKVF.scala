package me.mappers.ikvf

import me.mappers.ikvf.decoder.{Decoder, DecodingResult}

/**
  * Imaginary Key Value Format
  */
object IKVF {

  def decode[T: Decoder](raw: String): DecodingResult[Seq[T]] =
    for {
      ast <- AST.parse(raw)
      result <- Decoder[Seq[T]].decode(ast)
    } yield result

}
