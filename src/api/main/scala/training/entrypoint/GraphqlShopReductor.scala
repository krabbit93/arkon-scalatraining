package training.entrypoint

import akka.actor.ActorSystem
import akka.event.Logging
import cats.effect.IO
import doobie.implicits._
import training.modules.shops._

final class GraphqlShopReductor(
    private val shopRepository: ShopRepository,
    private val shopTypeRepository: ShopTypeRepository,
    private val stratumRepository: StratumRepository,
    private val commercialActivityRepository: CommercialActivityRepository
)(implicit system: ActorSystem)
    extends ShopReductor {
  private val log = Logging(system.eventStream, "reductor")

  override def shopsInRadius(radius: Int, lat: Double, long: Double, id: Option[Int] = None): IO[List[Shop]] =
    shopRepository.shopsInRadius(radius, lat, long, id).transact(DataAccess.xa)

  override def nearbyShops(limit: Int, lat: Double, long: Double, id: Option[Int] = None): IO[List[Shop]] =
    shopRepository.nearbyShops(limit, lat, long, id).transact(DataAccess.xa)

  override def all(limit: Int, offset: Int): IO[List[Shop]] =
    shopRepository.getAll(limit, offset).transact(DataAccess.xa)

  override def findShop(id: Int): IO[Option[Shop]] = shopRepository.find(id).transact(DataAccess.xa)

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
  ): IO[Int] = {
    shopRepository
      .createShop(
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
      .transact(DataAccess.xa)
      .map(_ => id)
  }

  override def getShopType(shopTypeId: Int): IO[Option[ShopType]] =
    shopTypeRepository.find(shopTypeId).transact(DataAccess.xa)

  override def findStratum(stratumId: Int): IO[Option[Stratum]] =
    stratumRepository.find(stratumId).transact(DataAccess.xa)

  override def findActivity(activityId: Int): IO[Option[CommercialActivity]] =
    commercialActivityRepository.find(activityId).transact(DataAccess.xa)
}
