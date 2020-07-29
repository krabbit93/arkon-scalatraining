package training

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}
import training.domain._

object Marshallers {
  implicit object GraphMarshall extends Decoder[GraphqlRequest] {
    override def apply(c: HCursor): Result[GraphqlRequest] = {
      val query = c.downField("query").as[String] match {
        case Left(_) => throw new IllegalArgumentException
        case Right(value) => value
      }

      val operationName = c.downField("operationName").as[String] match {
        case Left(_) => None
        case Right(value) => Option(value)
      }

      val variables = c.downField("variables").as[Map[String, String]] match {
        case Left(_) => None
        case Right(value) => Option(value)
      }

      Right(GraphqlRequest(query, operationName, variables))
    }
  }

  implicit object ShopEncoder extends Encoder[CommercialActivity] {
    override def apply(a: CommercialActivity): Json = Json.obj(
        ("id", Json.fromInt(a.id)),
        ("name", Json.fromString(a.name))
    )
  }
}
