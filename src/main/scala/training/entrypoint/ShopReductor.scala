package training.entrypoint

import training.modules.shops._

trait ShopReductor {
  def getShopType(shopTypeId: Int): Option[ShopType]

  def findStratum(stratumId: Int): Option[Stratum]

  def findActivity(activityId: Int): Option[CommercialActivity]

  def shopsInRadius(radius: Int, lat: Double, long: Double, id: Option[Int] = None): List[Shop]

  def nearbyShops(limit: Int, lat: Double, long: Double, id: Option[Int] = None): List[Shop]

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
