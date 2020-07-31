package training.entrypoint

import akka.actor.ActorSystem
import akka.event.Logging
import training.modules.shops._

final class GraphqlShopReductor(
    private val shopRepository: ShopRepository,
    private val shopTypeRepository: ShopTypeRepository,
    private val stratumRepository: StratumRepository,
    private val commercialActivityRepository: CommercialActivityRepository
)(implicit system: ActorSystem)
    extends ShopReductor {
  private val log = Logging(system.eventStream, "reductor")

  override def shopsInRadius(radius: Int, lat: Double, long: Double, id: Int): List[Shop] =
    shopRepository.shopsInRadius(radius, lat, long, id)

  override def nearbyShops(limit: Int, lat: Double, long: Double, id: Int): List[Shop] =
    shopRepository.nearbyShops(limit, lat, long, id)

  override def all(limit: Int, offset: Int): List[Shop] = shopRepository.getAll(limit, offset)

  override def findShop(id: Int): Shop = shopRepository.find(id)

  override def createShop(
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
  ): Int = {
    shopRepository.createShop(
      id = id,
      businessName = businessName,
      name = name,
      activityId = activityId,
      stratumId = stratumId,
      address = address,
      phoneNumber = phoneNumber,
      email = email,
      website = website,
      shopTypeId = shopTypeId,
      position = position
    )
    id
  }

  override def getShopType(shopTypeId: Int): Option[ShopType] = shopTypeRepository.find(shopTypeId)

  override def findStratum(stratumId: Int): Option[Stratum] = stratumRepository.find(stratumId)

  override def findActivity(activityId: Int): Option[CommercialActivity] = commercialActivityRepository.find(activityId)
}
