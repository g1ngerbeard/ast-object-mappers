package me.parser.ikvf

import cats.implicits._
import me.parser.ikvf.decoder.DecodingError.ASTParsingFailed
import me.parser.ikvf.decoder.{Decoder, DecodingResult}

object AST {

  sealed trait IKVFValue

  case class Field(name: String, value: String) extends IKVFValue

  case class EmptyField(name: String) extends IKVFValue

  case class IKVFObject(fields: Map[String, Field]) extends IKVFValue

  case class IKVFArray(objects: Seq[IKVFObject]) extends IKVFValue

  def parse(raw: String): DecodingResult[IKVFValue] =
    raw
      .split("\\n{2,}")
      .toVector
      .traverse(
        _.trim.split("[\n ]").toVector
          .traverse {
            case s"$key:$value" => Right(Field(key, value))
            case invalid        => Left(ASTParsingFailed(s"Invalid field format: $invalid"))
          }
          .map { fields =>
            IKVFObject(fields.map(f => f.name -> f).toMap)
          }
      )
      .map(IKVFArray)

  def stringify(value: IKVFValue): String = value match {
    case IKVFArray(objects) => objects.map(stringify).mkString("\n\n")
    case IKVFObject(fields) => fields.values.map(stringify).mkString(" ")
    case Field(name, value) => s"$name:$value"
    case EmptyField(_)      => ""
  }

}
