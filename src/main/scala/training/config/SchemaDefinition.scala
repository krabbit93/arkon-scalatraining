package training.config

import com.typesafe.config.ConfigFactory
import sangria.marshalling.circe._
import sangria.schema.{fields, Argument, Field, FloatType, InputField, InputObjectType, IntType, ListType, ObjectType, OptionInputType, OptionType, Schema, StringType}
import training.entrypoint.ShopReductor
import training.modules.shops._

case class ShopPayload(id: Int)

object SchemaDefinition {

  implicit def mapToPosition(input: Map[String, Double]): Position =
    Position(latitude = input("latitude"), longitude = input("longitude"))

  private val schemaConf = ConfigFactory.load("schema")

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
        Field("activity", OptionType(activity), resolve = c => c.ctx.findActivity(c.value.activityId)),
        Field("stratum", OptionType(stratum), resolve = c => c.ctx.findStratum(c.value.stratumId)),
        Field("address", StringType, resolve = _.value.address),
        Field("phoneNumber", OptionType(StringType), resolve = _.value.phoneNumber),
        Field("email", OptionType(StringType), resolve = _.value.email),
        Field("website", OptionType(StringType), resolve = _.value.website),
        Field("shopType", OptionType(shopType), resolve = c => c.ctx.getShopType(c.value.shopTypeId)),
        Field("lat", FloatType, resolve = _.value.position.latitude),
        Field("long", FloatType, resolve = _.value.position.longitude),
        Field(
          "nearbyShops",
          ListType(shop),
          resolve = c => c.ctx.nearbyShops(c.arg[Int]("limit"), c.value.position.latitude, c.value.position.longitude),
          arguments = List(Argument("limit", IntType, defaultValue = schemaConf.getInt("defaultNearbyShops")))
        ),
        Field(
          "shopsInRadius",
          ListType(shop),
          resolve =
            c => c.ctx.shopsInRadius(c.arg[Int]("radius"), c.value.position.latitude, c.value.position.longitude),
          arguments = List(Argument("radius", IntType, defaultValue = schemaConf.getInt("shopsInRadius.defaultRadio")))
        )
      )
  )

  val query: ObjectType[ShopReductor, Unit] = ObjectType(
    "Query",
    fields[ShopReductor, Unit](
      Field("shop", shop, resolve = c => c.ctx.findShop(c.arg[Int]("id")), arguments = List(Argument("id", IntType))),
      Field(
        "shops",
        ListType(shop),
        resolve = c => c.ctx.all(c.arg[Int]("limit"), c.arg[Int]("offset")),
        arguments = List(
          Argument("limit", IntType, defaultValue = schemaConf.getInt("shopQuery.defaultLimit")),
          Argument("offset", IntType, defaultValue = schemaConf.getInt("shopQuery.defaultOffset"))
        )
      ),
      Field(
        "nearbyShops",
        ListType(shop),
        resolve = c => c.ctx.nearbyShops(c.arg[Int]("limit"), c.arg[Double]("lat"), c.arg[Double]("long")),
        arguments = List(
          Argument("limit", IntType, defaultValue = schemaConf.getInt("shopQuery.defaultLimit")),
          Argument("lat", FloatType),
          Argument("long", FloatType)
        )
      ),
      Field(
        "shopsInRadius",
        ListType(shop),
        resolve = c => c.ctx.shopsInRadius(c.arg[Int]("radius"), c.arg[Double]("lat"), c.arg[Double]("long")),
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
          val id = c.ctx.createShop(
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
          ShopPayload(id)
        }
      )
    )
  )

  val schema: Schema[ShopReductor, Unit] = Schema(query, Some(mutation))

}
