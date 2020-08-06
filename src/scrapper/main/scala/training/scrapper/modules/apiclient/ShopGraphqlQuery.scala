package training.scrapper.modules.apiclient

import io.circe.syntax._
import io.circe.{Encoder, Json}
import training.domain.ShopRaw

object ShopGraphqlQuery {
  private val alias = "shop"
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

  def apply(shops: Seq[ShopRaw]): GraphqlQuery = {
    val operationName = "ShopGraphqlQuery"
    val query =
      f"""
         |mutation ShopGraphqlQuery(${createInputs(shops)}) {
         |  ${simpleMutations(shops)}
         |}
         |""".stripMargin
    val variables = createMap(shops).asJson
    GraphqlQuery(query, operationName, variables)
  }

  private def createMap(shops: Seq[ShopRaw]): Map[String, ShopRaw] =
    shops.map(shop => f"input$alias${shops.indexOf(shop)}" -> shop).toMap

  private def createInputs(shops: Seq[ShopRaw]): String =
    shops
      .map(shop => f"${"$"}input$alias${shops.indexOf(shop)}: CreateShopInput!")
      .mkString(", ")

  private def simpleMutations(shops: Seq[ShopRaw]): String =
    shops
      .map(shop => f"""
                      |$alias${shops.indexOf(shop)}: createShop(input: ${"$"}input$alias${shops.indexOf(shop)}) {
                      |  id
                      |}
                      |""".stripMargin)
      .mkString
}
