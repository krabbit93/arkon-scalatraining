package training

import akka.event.jul.Logger


case class CommercialActivity(id: Int, name: String)

case class ShopType(id: Int, name: String)

case class Stratum(id: Int, name: String)

case class Shop(
                 id: Int, name: String, businessName: String,
                 activity: CommercialActivity, stratum: Stratum,
                 address: String, phoneNumber: String, email: String,
                 website: String, shopType: ShopType, position: (Long, Long)
               )

class CommercialActivityRepo {

  import CommercialActivityRepo._

  def createShop(
                  businessName: String,
                  name: String,
                  activityId: Int,
                  stratumId: Int,
                  address: String,
                  phoneNumber: String,
                  email: String,
                  website: String,
                  shopTypeId: Int,
                  position: (Long, Long)
                ): Shop = {
    log.info("Shop method called")
    Shop(
      1, name, businessName, CommercialActivity(activityId, "Fake"),
      Stratum(stratumId, "Fake"), address, phoneNumber, email,
      website, ShopType(shopTypeId, "Fake"), position
    )
  }

  def getAll(): List[Shop] = {
    log.info("Get all shops called")
    List()
  }
}

object CommercialActivityRepo {
  val log = Logger(CommercialActivityRepo.getClass, "")
}