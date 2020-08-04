package training.entrypoint

import cats.effect.IO
import training.modules.shops._

trait ShopReductor {
  def getShopType(shopTypeId: Int): IO[Option[ShopType]]

  def findStratum(stratumId: Int): IO[Option[Stratum]]

  def findActivity(activityId: Int): IO[Option[CommercialActivity]]

  def shopsInRadius(radius: Int, lat: Double, long: Double, id: Option[Int] = None): IO[List[Shop]]

  def nearbyShops(limit: Int, lat: Double, long: Double, id: Option[Int] = None): IO[List[Shop]]

  def all(limit: Int, offset: Int): IO[List[Shop]]

  def findShop(id: Int): IO[Option[Shop]]

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
  ): IO[Int]
}
