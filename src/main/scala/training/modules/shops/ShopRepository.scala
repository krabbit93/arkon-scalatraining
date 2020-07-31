package training.modules.shops

class ShopRepository {

  def createShop(
      businessName: String,
      name: String,
      activityId: Int,
      stratumId: Int,
      address: String,
      phoneNumber: String,
      email: String,
      website: String,
      shopTypeId: Int,
      position: Position
  ): Shop = {
    Shop(
      1,
      name,
      businessName,
      1,
      2,
      address,
      phoneNumber,
      email,
      website,
      3,
      position
    )
  }

  def getAll(): List[Shop] = {
    List()
  }
}
