package me.parser.ikvf.decoder

import cats.implicits._
import magnolia.{CaseClass, Magnolia}
import me.parser.ikvf.AST._
import me.parser.ikvf.decoder.DecodingError.{FieldDecodingError, ObjectDecodingError}

object semiauto {

  type Typeclass[T] = Decoder[T]

  def combine[T](caseClass: CaseClass[Typeclass, T]): Typeclass[T] = {
    case IKVFObject(fields) =>
      caseClass
        .constructEither { param =>
          val label = param.label
          val field = fields.getOrElse(label, EmptyField(label))
          param.typeclass.decode(field)
        }
        .leftMap(ObjectDecodingError)
    case other => FieldDecodingError(s"Expected IKVFObject got ${other.getClass}").asLeft
  }

  def deriveDecoder[T]: Typeclass[T] = macro Magnolia.gen[T]

}
