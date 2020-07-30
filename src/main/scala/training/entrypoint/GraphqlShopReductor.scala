package training.entrypoint

import training.modules.shops.{Position, Shop, ShopRepository}

final class GraphqlShopReductor(private val shopRepository: ShopRepository) extends ShopReductor {

  override def createShop(
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
    shopRepository.createShop(
      businessName,
      name,
      activityId,
      stratumId,
      address,
      phoneNumber,
      email,
      website,
      shopTypeId,
      position
    )
  }

  override def all(): List[Shop] = shopRepository.getAll()
}
