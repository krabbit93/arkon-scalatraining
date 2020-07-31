package training.entrypoint

import training.modules.shops._

trait ShopReductor {
  def getShopType(shopTypeId: Int): ShopType

  def findStratum(stratumId: Int): Stratum

  def findActivity(activityId: Int): CommercialActivity

  def shopsInRadius(radius: Int, lat: Double, long: Double, id: Int): List[Shop]

  def nearbyShops(limit: Int, lat: Double, long: Double, id: Int): List[Shop]

  def all(limit: Int, offset: Int): List[Shop]

  def findShop(id: Int): Shop

  def createShop(
      id: Int,
      businessName: Option[String],
      name: String,
      activityId: Option[Int],
      stratumId: Option[Int],
      address: String,
      phoneNumber: Option[String],
      email: Option[String],
      website: Option[String],
      shopTypeId: Option[Int],
      position: Position
  ): Int
}
