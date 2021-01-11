package me.mappers.ikvf.decoder

sealed trait DecodingError

object DecodingError {

  case class ASTParsingFailed(reason: String) extends DecodingError

  case class ArrayDecodingError(errorsWithIdx: Seq[(Int, DecodingError)]) extends DecodingError

  case class ObjectDecodingError(errors: Seq[DecodingError]) extends DecodingError

  case class FieldDecodingError(reason: String) extends DecodingError

}
