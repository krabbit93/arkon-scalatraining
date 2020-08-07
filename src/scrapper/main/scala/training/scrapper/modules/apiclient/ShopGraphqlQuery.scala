package training.scrapper.modules.apiclient

import io.circe.syntax._
import training.domain.ShopRaw
import training.scrapper.config.marshallers._

object ShopGraphqlQuery {
  private val alias = "shop"

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
    shops.map(shop => name(shop, shops) -> shop).toMap

  private def createInputs(shops: Seq[ShopRaw]): String =
    shops
      .map(shop => f"${$name(shop, shops)}: CreateShopInput!")
      .mkString(", ")

  private def simpleMutations(shops: Seq[ShopRaw]): String =
    shops
      .map(shop => f"""
                      |${name(shop, shops)}: createShop(input: ${$name(shop, shops)}) {
                      |  id
                      |}
                      |""".stripMargin)
      .mkString

  private def name(shop: ShopRaw, shops: Seq[ShopRaw]): String  = f"input$alias${shops.indexOf(shop)}"
  private def $name(shop: ShopRaw, shops: Seq[ShopRaw]): String = f"${"$"}${name(shop, shops)}"
}
