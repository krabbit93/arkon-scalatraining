package training.entrypoint

import akka.event.Logging
import training.modules.shops._
import training.config.dependencies._
import training.domain._
import doobie._
import doobie.implicits._
import cats._
import cats.effect._
import cats.implicits._

final class GraphqlShopReductor()(implicit
    shopRepository: ShopRepository,
    shopTypeRepository: ShopTypeRepository,
    stratumRepository: StratumRepository,
    commercialActivityRepository: CommercialActivityRepository
) extends ShopReductor {
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
      activity: Option[String],
      stratum: Option[String],
      address: String,
      phoneNumber: Option[String],
      email: Option[String],
      website: Option[String],
      shopType: Option[String],
      position: Position
  ): IO[Int] = {
    (for {
      activityId <- commercialActivityRepository.getIdOrCreate(activity)
      stratumId  <- stratumRepository.getIdOrCreate(stratum)
      shopTypeId <- shopTypeRepository.getIdOrCreate(shopType)
      operation <-
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
    } yield operation)
      .transact(DataAccess.xa)
  }

  override def getShopType(shopTypeId: Int): IO[Option[ShopType]] =
    shopTypeRepository.find(shopTypeId).transact(DataAccess.xa)

  override def findStratum(stratumId: Int): IO[Option[Stratum]] =
    stratumRepository.find(stratumId).transact(DataAccess.xa)

  override def findActivity(activityId: Int): IO[Option[CommercialActivity]] =
    commercialActivityRepository.find(activityId).transact(DataAccess.xa)
}
