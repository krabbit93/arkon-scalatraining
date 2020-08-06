package training.scrapper.modules.apiclient

import com.typesafe.scalalogging.Logger
import training.BaseSpec
import training.domain.ShopRaw

class GraphqlApiClientSpec extends BaseSpec {
  private val logger = Logger[GraphqlApiClientSpec]

  "ShopGraphqlQuery" should "generate a correct query" in {
    logger.info(
      "{}",
      ShopGraphqlQuery(
        Seq(
          ShopRaw(
            id = 1,
            name = "name",
            businessName = Some("bussines"),
            activity = Some("activity"),
            stratum = Some("stratum"),
            address = "address",
            phoneNumber = Some("phoneNumber"),
            email = Some("email@gmail.com"),
            website = None,
            shopType = Some("shopType"),
            lat = 1.0,
            long = 1.0
          ),
          ShopRaw(
            id = 2,
            name = "name-1",
            businessName = Some("bussines-1"),
            activity = Some("activity-1"),
            stratum = Some("stratum-1"),
            address = "address",
            phoneNumber = Some("phoneNumber-1"),
            email = Some("email@gmail.com-1"),
            website = None,
            shopType = Some("shopType-1"),
            lat = 1.0,
            long = 1.0
          )
        )
      )
    )
  }
}
