package training.modules.shops

class ShopRepository {

  def createShop(businessName: String, name: String, activityId: Int, stratumId: Int, address: String,
                 phoneNumber: String, email: String, website: String, shopTypeId: Int,
                 position: (Long, Long)): Shop = {
    Shop(1, name, businessName, CommercialActivity(activityId, "Fake"), Stratum(stratumId, "Fake"),
      address, phoneNumber, email, website, ShopType(shopTypeId, "Fake"), position)
  }

  def getAll(): List[Shop] = {
    List()
  }
}