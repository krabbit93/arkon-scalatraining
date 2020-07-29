package training.modules.shops

case class CommercialActivity(id: Int, name: String)

case class ShopType(id: Int, name: String)

case class Stratum(id: Int, name: String)

case class Shop(
                 id: Int, name: String, businessName: String,
                 activity: CommercialActivity, stratum: Stratum,
                 address: String, phoneNumber: String, email: String,
                 website: String, shopType: ShopType, position: (Long, Long)
               )