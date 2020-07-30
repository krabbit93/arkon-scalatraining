package training.entrypoint

import training.modules.shops.{Position, Shop}

trait ShopReductor {
  def shopsInRadius(radius: Int, lat: Double, long: Double): List[Shop]

  def nearbyShops(limit: Int, lat: Double, long: Double): List[Shop]

  def all(limit: Int, offset: Int): List[Shop]

  def findShop(id: Int): Shop

  def createShop(
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
