package training.modules.shops

import org.postgis.PGgeometry

case class CommercialActivity(id: Int, name: String)

case class ShopType(id: Int, name: String)

case class Stratum(id: Int, name: String)

case class Position(latitude: Double, longitude: Double)

case class Shop(
    id: Int,
    name: String,
    businessName: String,
    activityId: Int,
    stratumId: Int,
    address: String,
    phoneNumber: String,
    email: String,
    website: String,
    shopTypeId: Int,
    position: Position
)

object Shop {
  def apply(
      id: Int,
      name: String,
      businessName: String,
      activityId: Int,
      stratumId: Int,
      address: String,
      phoneNumber: String,
      email: String,
      website: String,
      shopTypeId: Int,
      lat: Double,
      long: Double
  ): Shop =
    new Shop(
      id,
      name,
      businessName,
      activityId,
      stratumId,
      address,
      phoneNumber,
      email,
      website,
      shopTypeId,
      Position(
        lat,
        long
      )
    )
}
