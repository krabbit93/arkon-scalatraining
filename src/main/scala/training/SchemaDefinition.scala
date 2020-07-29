package training

import sangria.schema._
import training.domain._
import training.entrypoint.ShopReductor

object SchemaDefinition {

  val activity = ObjectType(
    "Activity",
    "A Commercial activity",
    fields[Unit, CommercialActivity](
      Field("id", IntType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name)
    )
  )

  val shopType = ObjectType(
    "ShopType",
    "Type of shop",
    fields[Unit, ShopType](
      Field("id", IntType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name)
    )
  )

  val stratum = ObjectType(
    "Stratum",
    "stratum",
    fields[Unit, Stratum](
      Field("id", IntType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name)
    )
  )

  val position = ObjectType(
    "Position",
    "A latitude and longitude",
    fields[Unit, (Long, Long)](
      Field("latitude", LongType, resolve = _.value._1),
      Field("longitude", LongType, resolve = _.value._2)
    )
  )

  val shop = ObjectType(
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

  val query = ObjectType("Query", fields[ShopReductor, Unit](
    Field("shops", ListType(shop), resolve = _.ctx.all())
  ))

  val mutation = ObjectType("Mutation",
    fields[ShopReductor, Unit](
      Field("createShop", shop,
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
          Argument("latitude", LongType),
          Argument("longitude", LongType)
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
            (c.arg[Long]("latitude"), c.arg[Long]("longitude"))
          )
        })
    ))

  val schema = Schema(query, Option(mutation))

}
