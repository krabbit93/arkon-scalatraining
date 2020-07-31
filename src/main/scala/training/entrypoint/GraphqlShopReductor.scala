package training.entrypoint

import akka.actor.ActorSystem
import akka.event.Logging
import training.modules.shops._

final class GraphqlShopReductor(private val shopRepository: ShopRepository)(implicit system: ActorSystem)
    extends ShopReductor {
  private val log = Logging(system.eventStream, "reductor")

  override def shopsInRadius(radius: Int, lat: Double, long: Double): List[Shop] = {
    log.info("Se buscan las cercanas")
    List()
  }

  override def nearbyShops(limit: Int, lat: Double, long: Double): List[Shop] = List()

  override def all(limit: Int, offset: Int): List[Shop] = List()

  override def findShop(id: Int): Shop =
    Shop(
      id = 1,
      name = "fake",
      businessName = "fake",
      activityId = 1,
      stratumId = 1,
      address = "fake",
      phoneNumber = "31241232",
      email = "sagt@asda.com",
      website = "algo.com",
      shopTypeId = 1,
      position = Position(3.4523453, 3.3454534)
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

  override def getShopType(shopTypeId: Int): ShopType = ShopType(1, "12341")

  override def findStratum(stratumId: Int): Stratum = {
    log.info("Se realiza la consulta de stratum")
    Stratum(3, "Stratum")
  }

  override def findActivity(activityId: Int): CommercialActivity = CommercialActivity(1, "a Comercial")
}
