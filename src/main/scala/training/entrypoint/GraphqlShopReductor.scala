package training.entrypoint

import training.modules.shops._

final class GraphqlShopReductor(private val shopRepository: ShopRepository) extends ShopReductor {
  override def shopsInRadius(radius: Int, lat: Double, long: Double): List[Shop] = List()

  override def nearbyShops(limit: Int, lat: Double, long: Double): List[Shop] = List()

  override def all(limit: Int, offset: Int): List[Shop] = List()

  override def findShop(id: Int): Shop =
    Shop(
      id = 1,
      name = "fake",
      businessName = "fake",
      activity = CommercialActivity(1, "Fake"),
      stratum = Stratum(1, "Fake"),
      address = "fake",
      phoneNumber = "31241232",
      email = "sagt@asda.com",
      website = "algo.com",
      shopType = ShopType(1, "dasdas"),
      position = Position(3.4523453, 3.3454534),
      nearbyShops = List(),
      shopsInRadius = List()
    )

  override def createShop(
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
  ): Int = 1
}
