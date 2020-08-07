package training.scrapper.modules.webcollector
import akka.http.javadsl.model.headers.Accept
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.unmarshalling.Unmarshal
import cats.effect.IO
import cats.implicits.catsSyntaxApplicativeId
import training.domain.ShopRaw
import training.scrapper.config.dependencies._
import training.scrapper.config.marshallers._
import training.scrapper.modules.shared.{Query, ScrapperError}

import scala.concurrent.Future

final class INEGIWebCollector extends WebCollector {

  override def apply(query: Query): IO[Either[ScrapperError, Seq[ShopRaw]]] = {
    IO.fromFuture(
      Http()
        .singleRequest(Get(query.build()).withHeaders(Accept.create(MediaTypes.`application/json`)))
        .flatMap(res =>
          if (res.status.isSuccess())
            Unmarshal(res).to[Either[ScrapperError, Seq[ShopRaw]]]
          else
            Unmarshal(res.entity).to[String].flatMap(str => Future(Left(ScrapperError(str))))
        )
        .pure[IO]
    )
  }
}
