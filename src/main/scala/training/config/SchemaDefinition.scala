package training.config

import cats.effect.{IO, LiftIO}
import com.typesafe.config.ConfigFactory
import sangria.marshalling.circe._
import sangria.schema.{fields, Argument, Field, FloatType, InputField, InputObjectType, IntType, ListType, ObjectType, OptionInputType, OptionType, Schema, StringType}
import training.entrypoint.ShopReductor
import training.modules.shops._

import scala.concurrent.Future

case class ShopPayload(id: Int)

object SchemaDefinition {
  private val schemaConf = ConfigFactory.load("schema")

  implicit val toOption: LiftIO[Future] = new LiftIO[Future] {
    override def liftIO[A](ioa: IO[A]): Future[A] = {
      ioa.unsafeToFuture()
    }
  }

  val activity: ObjectType[Unit, CommercialActivity] = ObjectType(
    "ActivityType",
    "A Commercial activity",
    fields[Unit, CommercialActivity](
      Field("id", IntType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name)
    )
  )

  val shopType: ObjectType[Unit, ShopType] = ObjectType(
    "ShopTypeType",
    "Type of shop",
    fields[Unit, ShopType](
      Field("id", IntType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name)
    )
  )

  val stratum: ObjectType[Unit, Stratum] = ObjectType(
    "StratumType",
    "stratum",
    fields[Unit, Stratum](
      Field("id", IntType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name)
    )
  )

  val shop: ObjectType[ShopReductor, Shop] = ObjectType(
    "ShopType",
    "A shop",
    () =>
      fields[ShopReductor, Shop](
        Field("id", IntType, resolve = _.value.id),
        Field("name", StringType, resolve = _.value.name),
        Field("businessName", OptionType(StringType), resolve = _.value.businessName),
        Field(
          "activity",
          OptionType(activity),
          resolve = c =>
            (c.value.activityId match {
              case Some(value) => c.ctx.findActivity(value)
              case None        => IO(None)
            }).to[Future]
        ),
        Field(
          "stratum",
          OptionType(stratum),
          resolve = c =>
            (c.value.stratumId match {
              case Some(value) => c.ctx.findStratum(value)
              case None        => IO(None)
            }).to[Future]
        ),
        Field("address", StringType, resolve = _.value.address),
        Field("phoneNumber", OptionType(StringType), resolve = _.value.phoneNumber),
        Field("email", OptionType(StringType), resolve = _.value.email),
        Field("website", OptionType(StringType), resolve = _.value.website),
        Field(
          "shopType",
          OptionType(shopType),
          resolve = c =>
            (c.value.shopTypeId match {
              case Some(value) => c.ctx.getShopType(value)
              case None        => IO(None)
            }).to[Future]
        ),
        Field("lat", FloatType, resolve = _.value.position.latitude),
        Field("long", FloatType, resolve = _.value.position.longitude),
        Field(
          "nearbyShops",
          ListType(shop),
          resolve = c =>
            c.ctx
              .nearbyShops(
                c.arg[Int]("limit"),
                c.value.position.latitude,
                c.value.position.longitude,
                Some(c.value.id)
              )
              .to[Future],
          arguments = List(Argument("limit", IntType, defaultValue = schemaConf.getInt("defaultNearbyShops")))
        ),
        Field(
          "shopsInRadius",
          ListType(shop),
          resolve = c =>
            c.ctx
              .shopsInRadius(
                c.arg[Int]("radius"),
                c.value.position.latitude,
                c.value.position.longitude,
                Some(c.value.id)
              )
              .to[Future],
          arguments = List(Argument("radius", IntType, defaultValue = schemaConf.getInt("shopsInRadius.defaultRadio")))
        )
      )
  )

  val query: ObjectType[ShopReductor, Unit] = ObjectType(
    "Query",
    fields[ShopReductor, Unit](
      Field(
        "shop",
        OptionType(shop),
        resolve = c => c.ctx.findShop(c.arg[Int]("id")).to[Future],
        arguments = List(Argument("id", IntType))
      ),
      Field(
        "shops",
        ListType(shop),
        resolve = c => c.ctx.all(c.arg[Int]("limit"), c.arg[Int]("offset")).to[Future],
        arguments = List(
          Argument("limit", IntType, defaultValue = schemaConf.getInt("shopQuery.defaultLimit")),
          Argument("offset", IntType, defaultValue = schemaConf.getInt("shopQuery.defaultOffset"))
        )
      ),
      Field(
        "nearbyShops",
        ListType(shop),
        resolve = c => c.ctx.nearbyShops(c.arg[Int]("limit"), c.arg[Double]("lat"), c.arg[Double]("long")).to[Future],
        arguments = List(
          Argument("limit", IntType, defaultValue = schemaConf.getInt("shopQuery.defaultLimit")),
          Argument("lat", FloatType),
          Argument("long", FloatType)
        )
      ),
      Field(
        "shopsInRadius",
        ListType(shop),
        resolve =
          c => c.ctx.shopsInRadius(c.arg[Int]("radius"), c.arg[Double]("lat"), c.arg[Double]("long")).to[Future],
        arguments = List(
          Argument("radius", IntType, defaultValue = schemaConf.getInt("shopsInRadius.defaultRadio")),
          Argument("lat", FloatType),
          Argument("long", FloatType)
        )
      )
    )
  )

  val createShopPayload: ObjectType[Unit, ShopPayload] = ObjectType(
    "CreateShopPayload",
    fields[Unit, ShopPayload](
      Field("id", IntType, resolve = _.value.id)
    )
  )

  val createShopInput: Argument[Map[String, Any]] = Argument(
    "input",
    InputObjectType[Map[String, Any]](
      "CreateShopInput",
      fields = List(
        InputField("id", IntType),
        InputField("name", StringType),
        InputField("businessName", OptionInputType(StringType)),
        InputField("activity", OptionInputType(IntType)),
        InputField("stratum", OptionInputType(IntType)),
        InputField("address", StringType),
        InputField("phoneNumber", OptionInputType(StringType)),
        InputField("email", OptionInputType(StringType)),
        InputField("website", OptionInputType(StringType)),
        InputField("shopType", OptionInputType(IntType)),
        InputField("lat", FloatType),
        InputField("long", FloatType)
      )
    )
  )

  val mutation: ObjectType[ShopReductor, Unit] = ObjectType(
    "Mutation",
    fields[ShopReductor, Unit](
      Field(
        "createShop",
        createShopPayload,
        arguments = List(createShopInput),
        resolve = c => {
          val values = c.arg[Map[String, Any]]("input")
          c.ctx
            .createShop(
              values("id").asInstanceOf[Int],
              values.getOrElse("businessName", None).asInstanceOf[Option[String]],
              values("name").asInstanceOf[String],
              values.getOrElse("activity", None).asInstanceOf[Option[Int]],
              values.getOrElse("stratum", None).asInstanceOf[Option[Int]],
              values("address").asInstanceOf[String],
              values.getOrElse("phoneNumber", None).asInstanceOf[Option[String]],
              values.getOrElse("email", None).asInstanceOf[Option[String]],
              values.getOrElse("website", None).asInstanceOf[Option[String]],
              values.getOrElse("shopType", None).asInstanceOf[Option[Int]],
              Position(values("lat").asInstanceOf[Double], values("long").asInstanceOf[Double])
            )
            .map(it => ShopPayload(it))
            .to[Future]
        }
      )
    )
  )

  val schema: Schema[ShopReductor, Unit] = Schema(query, Some(mutation))

}
