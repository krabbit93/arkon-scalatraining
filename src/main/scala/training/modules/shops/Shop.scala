package training.modules.shops

case class CommercialActivity(id: Int, name: String)

case class ShopType(id: Int, name: String)

case class Stratum(id: Int, name: String)

case class Position(latitude: Double, longitude: Double)

case class Shop(
    id: Int,
    name: String,
    businessName: Option[String],
    activityId: Option[Int],
    stratumId: Option[Int],
    address: String,
    phoneNumber: Option[String],
    email: Option[String],
    website: Option[String],
    shopTypeId: Option[Int],
    position: Position
)

object Shop {
  def apply(
      id: Int,
      name: String,
      businessName: Option[String],
      activityId: Option[Int],
      stratumId: Option[Int],
      address: String,
      phoneNumber: Option[String],
      email: Option[String],
      website: Option[String],
      shopTypeId: Option[Int],
      position: Position,
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
