package me.mappers.ikvf

package object decoder {

  type DecodingResult[T] = Either[DecodingError, T]

}
