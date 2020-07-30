package training.config

import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{fields, Argument, Field, FloatType, InputField, InputObjectType, IntType, ListType, ObjectType, Schema, StringType}
import training.entrypoint.ShopReductor
import training.modules.shops._

object SchemaDefinition {

  implicit def mapToPosition(input: Map[String, Double]): Position =
    Position(latitude = input("latitude"), longitude = input("longitude"))

  val activity: ObjectType[Unit, CommercialActivity] = ObjectType(
    "Activity",
    "A Commercial activity",
    fields[Unit, CommercialActivity](
      Field("id", IntType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name)
    )
  )

  val shopType: ObjectType[Unit, ShopType] = ObjectType(
    "ShopType",
    "Type of shop",
    fields[Unit, ShopType](
      Field("id", IntType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name)
    )
  )

  val stratum: ObjectType[Unit, Stratum] = ObjectType(
    "Stratum",
    "stratum",
    fields[Unit, Stratum](
      Field("id", IntType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name)
    )
  )

  val position: ObjectType[Unit, Position] = ObjectType(
    "Position",
    "A latitude and longitude",
    fields[Unit, Position](
      Field("latitude", FloatType, resolve = _.value.latitude),
      Field("longitude", FloatType, resolve = _.value.longitude)
    )
  )

  val shop: ObjectType[Unit, Shop] = ObjectType(
    "Shop",
    "A shop",
    fields[Unit, Shop](
      Field("id", IntType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name),
      Field("businessName", StringType, resolve = _.value.businessName),
      Field("activity", activity, resolve = _.value.activity),
      Field("stratum", stratum, resolve = _.value.stratum),
      Field("address", StringType, resolve = _.value.address),
      Field("phoneNumber", StringType, resolve = _.value.phoneNumber),
      Field("email", StringType, resolve = _.value.email),
      Field("website", StringType, resolve = _.value.website),
      Field("shopType", shopType, resolve = _.value.shopType),
      Field("position", position, resolve = _.value.position)
    )
  )

  val query: ObjectType[ShopReductor, Unit] = ObjectType(
    "Query",
    fields[ShopReductor, Unit](
      Field("shops", ListType(shop), resolve = _.ctx.all())
    )
  )

  private val positionArg: Argument[Map[String, Double]] = Argument(
    "position",
    InputObjectType[Map[String, Double]](
      "position",
      fields = List(
        InputField("latitude", FloatType),
        InputField("longitude", FloatType)
      )
    )
  )

  val mutation: ObjectType[ShopReductor, Unit] = ObjectType(
    "Mutation",
    fields[ShopReductor, Unit](
      Field(
        "createShop",
        shop,
        arguments = List(
          Argument("businessName", StringType),
          Argument("name", StringType),
          Argument("activityId", IntType),
          Argument("stratumId", IntType),
          Argument("address", StringType),
          Argument("phoneNumber", StringType),
          Argument("email", StringType),
          Argument("website", StringType),
          Argument("shopTypeId", IntType),
          positionArg
        ),
        resolve = c => {
          c.ctx.createShop(
            c.arg[String]("businessName"),
            c.arg[String]("name"),
            c.arg[Int]("activityId"),
            c.arg[Int]("stratumId"),
            c.arg[String]("address"),
            c.arg[String]("phoneNumber"),
            c.arg[String]("email"),
            c.arg[String]("website"),
            c.arg[Int]("shopTypeId"),
            c.arg[Map[String, Double]](positionArg)
          )
        }
      )
    )
  )

  val schema: Schema[ShopReductor, Unit] = Schema(query, Option(mutation))

}
