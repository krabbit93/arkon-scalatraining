package training.config

import io.circe.Decoder.Result
import io.circe.{Decoder, HCursor, Json}
import training.GraphqlRequest

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

      val variables = c.downField("variables").as[Json] match {
        case Left(_) => Json.fromFields(Map())
        case Right(value) => value
      }

      Right(GraphqlRequest(query, operationName, variables))
    }
  }
}
