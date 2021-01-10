package me.mappers.ikvf.decoder

import cats.implicits._
import me.mappers.ikvf.AST._
import me.mappers.ikvf.decoder.DecodingError.FieldDecodingError

object Decoder {

  def apply[T: Decoder]: Decoder[T] = implicitly[Decoder[T]]

  implicit def sequenceDecoder[T: Decoder]: Decoder[Seq[T]] = {
    case IKVFArray(objects) => objects.traverse(Decoder[T].decode)
    case unexpected         => FieldDecodingError(s"Expected IKVFArray got ${unexpected.getClass}").asLeft
  }

  implicit def optionalFieldValueDecoder[T: Decoder]: Decoder[Option[T]] = {
    case EmptyField(_) => Option.empty.asRight
    case field: Field  => Decoder[T].decode(field).map(Option(_))
    case unexpected    => FieldDecodingError(s"Expected Field or EmptyField got ${unexpected.getClass}").asLeft
  }

  def fieldDecoder[T: Decoder](name: String): Decoder[T] = {
    case IKVFObject(fields) =>
      val field = fields.getOrElse(name, EmptyField(name))
      Decoder[T].decode(field)
    case unexpected => FieldDecodingError(s"Expected IKVFObject got ${unexpected.getClass}").asLeft
  }

  def fieldValueDecoder[T](p: String => Option[T]): Decoder[T] = {
    case Field(name, value) => p(value).toRight(FieldDecodingError(s"Invalid value for the field $name: $value"))
    case EmptyField(name)   => FieldDecodingError(s"Non optional field $name is empty").asLeft
    case unexpected         => FieldDecodingError(s"Expected Field got ${unexpected.getClass}").asLeft
  }

  implicit val intFieldValueDecoder: Decoder[Int] = fieldValueDecoder(_.toIntOption)

  implicit val longFieldValueDecoder: Decoder[Long] = fieldValueDecoder(_.toLongOption)

  implicit val doubleFieldValueDecoder: Decoder[Double] = fieldValueDecoder(_.toDoubleOption)

  implicit val floatFieldValueDecoder: Decoder[Float] = fieldValueDecoder(_.toFloatOption)

  implicit val booleanFieldValueDecoder: Decoder[Boolean] = fieldValueDecoder(_.toBooleanOption)

  implicit val stringFieldValueDecoder: Decoder[String] = fieldValueDecoder(Option(_))

}

trait Decoder[T] {

  def decode(value: IKVFValue): DecodingResult[T]

}
