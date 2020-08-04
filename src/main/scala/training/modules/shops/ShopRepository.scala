package training.modules.shops

import doobie.ConnectionIO
import doobie.implicits._

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
    sql"""insert into shop (id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, position)
          values ($id, $name, $businessName, $activityId, $stratumId, $address, $phoneNumber, $email, $website, $shopTypeId,
          ST_GeographyFromText('POINT(' || ${position.latitude} || ' ' || ${position.longitude} || ')'))""".update.run
  }

  def getAll(limit: Int, offset: Int): ConnectionIO[List[Shop]] =
    sql"""
    select id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, 
    st_x(st_pointfromwkb(position)) lat, st_y(st_pointfromwkb(position)) long from shop
    limit $limit offset $offset
    """
      .query[Shop]
      .to[List]

  def find(id: Int): ConnectionIO[Option[Shop]] = {
    sql"""
    select id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id, 
    st_x(st_pointfromwkb(position)) lat, st_y(st_pointfromwkb(position)) long from shop where id = $id;
    """
      .query[Shop]
      .option
  }

  def nearbyShops(limit: Int, lat: Double, long: Double, id: Option[Int]): ConnectionIO[List[Shop]] = {
    sql"""
    select id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id,
    st_x(st_pointfromwkb(position)) lat, st_y(st_pointfromwkb(position)) long
    from shop where (case when $id is null then true else id != $id end)
    order by position <-> ST_GeographyFromText('POINT(' || $lat || ' ' || $long || ')')
    limit $limit
    """
      .query[Shop]
      .to[List]
  }

  def shopsInRadius(radius: Int, lat: Double, long: Double, id: Option[Int]): ConnectionIO[List[Shop]] = {
    sql"""
        select id, name, business_name, activity_id, stratum_id, address, phone_number, email, website, shop_type_id,
        st_x(st_pointfromwkb(position)) lat, st_y(st_pointfromwkb(position)) long from shop
        where st_dwithin(ST_GeographyFromText('POINT(' || $lat || ' ' || $long || ')'), position, $radius)
        and (case when $id is null then true else id != $id end)
        order by st_distance(ST_GeographyFromText('POINT(' || $lat || ' ' || $long || ')'), position)
     """
      .query[Shop]
      .to[List]
  }
}
