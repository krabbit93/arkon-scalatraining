package training.scrapper.modules.apiclient
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Post
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.unmarshalling.Unmarshal
import cats.effect.IO
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import io.circe.syntax.EncoderOps
import io.circe.{Encoder, Json}
import training.domain.ShopRaw
import training.scrapper.config.dependencies._
import training.scrapper.modules.shared.ScrapperError

import scala.concurrent.Future

final class GraphqlApiClient extends ApiClient {
  private val logger = Logger[GraphqlApiClient]
  private val config = ConfigFactory.load("api")
  implicit val shopDecoder: Encoder[GraphqlQuery] = query =>
    Json.obj(
      ("query", query.query.asJson),
      ("operationName", query.operationName.asJson),
      ("variables", query.variables)
    )

  override def apply(shops: Seq[ShopRaw]): IO[Either[ScrapperError, String]] = {
    val query = ShopGraphqlQuery(shops).asJson

    IO.fromFuture(
      IO(
        Http()
          .singleRequest(
            Post(config.getString("url"))
              .withEntity(ContentTypes.`application/json`, query.noSpaces)
          )
          .flatMap(res => {
            if (res.status.isSuccess())
              Unmarshal(res.entity)
                .to[String]
                .flatMap(str => {
                  Future(Right(str))
                })
            else
              Unmarshal(res.entity)
                .to[String]
                .flatMap(str => Future(Left(ScrapperError(str))))
          })
      )
    )
  }
}
