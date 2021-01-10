package me.parser.ikvf

package object decoder {

  type DecodingResult[T] = Either[DecodingError, T]

}
