package training.scrapper.modules.webcollector
import akka.http.javadsl.model.headers.Accept
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.{HttpResponse, MediaTypes, StatusCode}
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.stream.Materializer
import cats.effect.IO
import io.circe.parser._
import io.circe.{Decoder, HCursor}
import training.domain.ShopRaw
import training.scrapper.config.dependencies._
import training.scrapper.modules.shared.{Query, ScrapperError}

import scala.concurrent.{ExecutionContext, Future}

final class INEGIWebCollector extends WebCollector {
  implicit val um: Unmarshaller[HttpResponse, Either[ScrapperError, Seq[ShopRaw]]] =
    new Unmarshaller[HttpResponse, Either[ScrapperError, Seq[ShopRaw]]] {
      override def apply(
          value: HttpResponse
      )(implicit ec: ExecutionContext, materializer: Materializer): Future[Either[ScrapperError, Seq[ShopRaw]]] = {
        Unmarshal(value.entity)
          .to[String]
          .flatMap(str => Future(parse(str)))
          .flatMap(
            {
              case Left(value) => Future(Left(ScrapperError("Error occurred in Json marshalling", Some(value))))
              case Right(value) =>
                value.as[Seq[ShopRaw]] match {
                  case Left(value)  => Future(Left(ScrapperError("Error occurred in Json marshalling", Some(value))))
                  case Right(value) => Future(Right(value))
                }
            }
          )
      }
    }

  private def checkOptionEmptyString(opt: Option[String]): Option[String] =
    opt match {
      case None        => None
      case Some(value) => if (value == "") None else Some(value)
    }

  implicit val decoder: Decoder[ShopRaw] = (c: HCursor) =>
    for {
      id           <- c.downField("Id").as[Int]
      name         <- c.downField("Nombre").as[String]
      businessName <- c.downField("Razon_social").as[Option[String]]
      activity     <- c.downField("Clase_actividad").as[Option[String]]
      stratum      <- c.downField("Estrato").as[Option[String]]
      street       <- c.downField("Calle").as[Option[String]]
      townHall     <- c.downField("Colonia").as[Option[String]]
      entity       <- c.downField("Ubicacion").as[Option[String]]
      phoneNumber  <- c.downField("Telefono").as[Option[String]]
      email        <- c.downField("Correo_e").as[Option[String]]
      website      <- c.downField("Sitio_internet").as[Option[String]]
      shopType     <- c.downField("Tipo").as[Option[String]]
      lat          <- c.downField("Latitud").as[Double]
      long         <- c.downField("Longitud").as[Double]
    } yield ShopRaw(
      id,
      name,
      checkOptionEmptyString(businessName),
      checkOptionEmptyString(activity),
      checkOptionEmptyString(stratum),
      (
        for {
          strStreet <- street
          strTown   <- townHall
          strEntity <- entity
        } yield f"$strStreet, $strTown, $strEntity"
      ).getOrElse(""),
      checkOptionEmptyString(phoneNumber),
      checkOptionEmptyString(email),
      checkOptionEmptyString(website),
      checkOptionEmptyString(shopType),
      lat,
      long
    )

  override def apply(query: Query): IO[Either[ScrapperError, Seq[ShopRaw]]] = {
    IO.fromFuture(
      IO(
        Http()
          .singleRequest(Get(query.build()).withHeaders(Accept.create(MediaTypes.`application/json`)))
          .flatMap(res =>
            if (res.status.isSuccess())
              Unmarshal(res).to[Either[ScrapperError, Seq[ShopRaw]]]
            else
              Unmarshal(res.entity).to[String].flatMap(str => Future(Left(ScrapperError(str))))
          )
      )
    )
  }
}
