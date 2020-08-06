package training.domain

case class ShopRaw(
    id: Int,
    name: String,
    businessName: Option[String],
    activity: Option[String],
    stratum: Option[String],
    address: String,
    phoneNumber: Option[String],
    email: Option[String],
    website: Option[String],
    shopType: Option[String],
    lat: Double,
    long: Double
)
