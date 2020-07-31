package training.modules.shops

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor
import doobie.postgres.pgisimplicits._
import doobie.postgres._
import doobie.postgres.implicits._

import scala.concurrent.ExecutionContext

class ShopRepository()(implicit executionContext: ExecutionContext) {
  implicit val cs = IO.contextShift(executionContext)

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5432/shops",
    "admin",
    "admin123"
  )

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
  ): Unit = {
    sql"""insert into shop (id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position)
          values (${id}, ${name}, ${businessName}, ${activityId}, ${stratumId}, ${address}, ${phoneNumber}, ${email}, ${website}, ${shopTypeId},
          ST_GeographyFromText('POINT(' || ${position.latitude} || ' ' || ${position.longitude} || ')'))""".update.run
      .transact(xa)
      .unsafeRunSync()
  }

  def getAll(limit: Int, offset: Int): List[Shop] =
    sql"""
    select id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, 
    st_x(st_pointfromwkb(position)) lat, st_y(st_pointfromwkb(position)) long from shop
    limit ${limit} offset ${offset}
    """
      .query[Shop]
      .to[List]
      .transact(xa)
      .unsafeRunSync()
}
