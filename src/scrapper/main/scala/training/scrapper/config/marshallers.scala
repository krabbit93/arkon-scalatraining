package training.scrapper.config

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.stream.Materializer
import io.circe.{Decoder, Encoder, HCursor, Json}
import io.circe.parser.parse
import io.circe.syntax.EncoderOps
import training.domain.ShopRaw
import training.scrapper.modules.apiclient.GraphqlQuery
import training.scrapper.modules.shared.ScrapperError

import scala.concurrent.{ExecutionContext, Future}

object marshallers {
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

  implicit val graphqlDecoder: Encoder[GraphqlQuery] = query =>
    Json.obj(
      ("query", query.query.asJson),
      ("operationName", query.operationName.asJson),
      ("variables", query.variables)
    )

  implicit val shopDecoder: Encoder[ShopRaw] = shop =>
    Json.obj(
      ("id", shop.id.asJson),
      ("name", shop.name.asJson),
      ("businessName", shop.businessName.asJson),
      ("activity", shop.activity.asJson),
      ("stratum", shop.stratum.asJson),
      ("address", shop.address.asJson),
      ("phoneNumber", shop.phoneNumber.asJson),
      ("email", shop.email.asJson),
      ("website", shop.website.asJson),
      ("shopType", shop.shopType.asJson),
      ("lat", shop.lat.asJson),
      ("long", shop.long.asJson)
    )

  private def checkOptionEmptyString(opt: Option[String]): Option[String] =
    opt match {
      case None        => None
      case Some(value) => if (value == "") None else Some(value)
    }
}
