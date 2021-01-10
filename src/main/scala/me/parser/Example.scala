package me.parser

import java.time.LocalDate

import cats.implicits._
import me.parser.ikvf.IKVF
import me.parser.ikvf.implicits._
import me.parser.ikvf.decoder.Decoder
import me.parser.ikvf.decoder.Decoder.{fieldDecoder, fieldValueDecoder}
import me.parser.ikvf.decoder.semiauto.deriveDecoder

import scala.util.Try

case class UserData(
    id: Int,
    birthDate: LocalDate,
    registrationDate: LocalDate,
    role: UserRole,
    score: Option[Int],
    userName: String
)

case class PaymentData(
    id: Long,
    fromAccount: String,
    toAccount: String,
    amountEur: Double,
    reference: Option[String]
)

sealed trait UserRole

object UserRole {

  case object Admin extends UserRole

  case object Client extends UserRole

  def parse(value: String): Option[UserRole] = value.toLowerCase match {
    case "admin"  => Admin.some
    case "client" => Client.some
    case _        => None
  }

}

object decoders {

  implicit val localDateDecoder: Decoder[LocalDate] = fieldValueDecoder(value => Try(LocalDate.parse(value)).toOption)

  implicit val userRoleDecoder: Decoder[UserRole] = fieldValueDecoder(UserRole.parse)

  // semiautomatic decoder derivation
  implicit val userDataDecoder: Decoder[UserData] = deriveDecoder[UserData]

  // remapping field names
  implicit val paymentDataDecoder: Decoder[PaymentData] =
    (
      fieldDecoder[Long]("id"),
      fieldDecoder[String]("from"),
      fieldDecoder[String]("to"),
      fieldDecoder[Double]("amt"),
      fieldDecoder[Option[String]]("ref")
    ).mapN(PaymentData)

}

object ExampleApp extends App {

  import decoders._

  val users = """id:1 birthDate:1989-01-09
     |registrationDate:2020-10-11 role:client
     |userName:user1
     |
     |id:2 birthDate:1995-04-01
     |registrationDate:2020-10-11 role:client
     |userName:user1 score:123""".stripMargin

  println(IKVF.decode[UserData](users))

  val payments = """id:1 birthDate:1989-01-09
    |registrationDate:2020-10-11 role:client
    |userName:user1
    |
    |id:2 birthDate:1995-04-01
    |registrationDate:2020-10-11 role:client
    |userName:user1 score:123""".stripMargin

  println(IKVF.decode[PaymentData](payments))

}
