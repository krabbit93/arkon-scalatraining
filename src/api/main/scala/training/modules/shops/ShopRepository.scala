package training.modules.shops

import doobie.ConnectionIO
import doobie.implicits._
import training.domain.{Position, Shop}

class ShopRepository() {

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
  ): ConnectionIO[Int] = {
    sql"""
          |INSERT INTO shop (id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position)
          |VALUES ($id, $name, $businessName, $activityId, $stratumId, $address, $phoneNumber, $email, $website, $shopTypeId,
          |ST_GeographyFromText('POINT(' || ${position.latitude} || ' ' || ${position.longitude} || ')'))
          |ON CONFLICT (id) DO UPDATE SET
          |name = Excluded.name, business_name = Excluded.business_name, activity_id = Excluded.activity_id, stratum_id = Excluded.stratum_id,
          |address = Excluded.address, phone_number = Excluded.phone_number, email = Excluded.email, website = Excluded.website,
          |shop_type_id = Excluded.shop_type_id, position = Excluded.position""".stripMargin.update
      .withUniqueGeneratedKeys[Int]("id")
  }

  def getAll(limit: Int, offset: Int): ConnectionIO[List[Shop]] =
    sql"""|
          |SELECT id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, 
          |st_x(st_pointfromwkb(position)) lat, st_y(st_pointfromwkb(position)) long FROM shop
          |LIMIT $limit OFFSET $offset
          |""".stripMargin
      .query[Shop]
      .to[List]

  def find(id: Int): ConnectionIO[Option[Shop]] = {
    sql"""|
          |SELECT id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, 
          |st_x(st_pointfromwkb(position)) lat, st_y(st_pointfromwkb(position)) long FROM shop WHERE id = $id;
          |""".stripMargin
      .query[Shop]
      .option
  }

  def nearbyShops(limit: Int, lat: Double, long: Double, id: Option[Int]): ConnectionIO[List[Shop]] = {
    sql"""
          |SELECT id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id,
          |st_x(st_pointfromwkb(position)) lat, st_y(st_pointfromwkb(position)) long
          |FROM shop WHERE (CASE WHEN $id IS NULL THEN true ELSE id != $id END)
          |ORDER BY position <-> ST_GeographyFromText('POINT(' || $lat || ' ' || $long || ')')
          |LIMIT $limit
          |""".stripMargin
      .query[Shop]
      .to[List]
  }

  def shopsInRadius(radius: Int, lat: Double, long: Double, id: Option[Int]): ConnectionIO[List[Shop]] = {
    sql"""
          |SELECT id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id,
          |st_x(st_pointfromwkb(position)) lat, st_y(st_pointfromwkb(position)) long FROM shop
          |WHERE st_dwithin(ST_GeographyFromText('POINT(' || $lat || ' ' || $long || ')'), position, $radius)
          |AND (CASE WHEN $id IS NULL THEN true ELSE id != $id END)
          |order by st_distance(ST_GeographyFromText('POINT(' || $lat || ' ' || $long || ')'), position)
          |""".stripMargin
      .query[Shop]
      .to[List]
  }
}
