## Mapping AST into data structures in Scala

### Motivation
To show how to build decoders (mappers?) between AST and Scala data structures using such mechanisms 
like typeclasses, typeclass instance derivation and how to make those instanses composable. 

This approach could be used e.g. with parsers for some proprietary data formats or to build object mappers for 
database client libraries.

### IKVF 
IKVF=Imaginary key value format

This is a toy text format from AoC 2020.

At the top there is an array of objects, each is separated by empty line, each object contains key value pairs `$k:$v`,
each pair is separated either by whitespace or by a new line.   

```
id:1 userName:John score:20000 
apiKey:000-12dafa11 

id:2 userName:winner2018 registrationDate:2018-06-11 score:12
 
id:3
userName:winner2018 
score:12323 
apiKey:bbb-foo-bar
```

### Parsing text format to AST object

This approach is inspired by numerous JSON libraries from Scala ecosystem (circe, play-json etc.)  

Parsing "pipeline":
```
raw text -> AST -> data objects
```

AST objects hierarchy:

```scala
sealed trait IKVFValue
case class IKVFArray(objects: Seq[IKVFObject]) extends IKVFValue
case class IKVFObject(fields: Map[String, Field]) extends IKVFValue
case class Field(name: String, value: String) extends IKVFValue
// this value is used as a signal to decoder that field is missing in the object
case class EmptyField(name: String) extends IKVFValue
```


### Mapping AST to Scala objects

Common interface for AST to Scala objects decoder is `Decoder[T]`:
```scala
type DecodingResult[T] = Either[DecodingError, T]

trait Decoder[T] {
  def decode(value: IKVFValue): DecodingResult[T]
}
```

### Decoder as typeclass

When decoding a text `Decoder` should be present in the context:
```scala
def decode[T: Decoder](rawText: String): DecodingResult[Seq[T]] = ???
```

This is a typeclass with a bunch of predefined instances, e.g:
```scala
implicit val intFieldValueDecoder: Decoder[Int] = fieldValueDecoder(_.toIntOption)
implicit val doubleFieldValueDecoder: Decoder[Double] = fieldValueDecoder(_.toDoubleOption)
```

We can define `Decoder` instances for our business objects, e.g. 
```scala
implicit val paymentDataDecoder: Decoder[PaymentData] =
    (value: IKVFValue) => 
      for {
        id     <- fieldDecoder[Long]("id").decode(value)
        from   <- fieldDecoder[String]("from").decode(value)
        to     <- fieldDecoder[String]("to").decode(value)
        amount <- fieldDecoder[Double]("amount").decode(value)
      } yield PaymentData(id, from, to, amount)
``` 

By adding cats `Monad` instance for our `Decoder`s we make them composable: 
```scala
implicit val paymentDataDecoder: Decoder[PaymentData] =
    (
      fieldDecoder[Long]("id"),
      fieldDecoder[String]("from"),
      fieldDecoder[String]("to"),
      fieldDecoder[Double]("amt"),
    ).mapN(PaymentData)
```

### Error handling and reporting
 
Decoding errors as sum type:
```scala
sealed trait DecodingError

object DecodingError {
  case class ASTParsingFailed(reason: String) extends DecodingError
  case class ArrayDecodingError(errorsWithIdx: Seq[(Int, DecodingError)]) extends DecodingError
  case class ObjectDecodingError(errors: Seq[DecodingError]) extends DecodingError
  case class FieldDecodingError(reason: String) extends DecodingError
}
```
Do not fail fast and report errors list in `ObjectDecodingError` and `ArrayDecodingError`

### Automatic derivation of typeclasses using Magnolia
`magnolia` library allows to implement automatic derivation of typeclasses without direct usage of Scala macros, e.g.:

```scala
implicit val userDataDecoder: Decoder[UserData] = deriveDecoder[UserData]
```
