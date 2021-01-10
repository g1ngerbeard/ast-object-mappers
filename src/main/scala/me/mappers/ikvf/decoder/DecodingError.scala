package me.mappers.ikvf.decoder

sealed trait DecodingError

object DecodingError {

  case class ASTParsingFailed(reason: String) extends DecodingError

  case class ObjectDecodingError(errors: List[DecodingError]) extends DecodingError

  case class FieldDecodingError(reason: String) extends DecodingError

}
