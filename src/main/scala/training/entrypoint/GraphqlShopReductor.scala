package training.entrypoint

import training.ShopRepository
import training.domain.Shop

final class GraphqlShopReductor(private val shopRepository: ShopRepository) extends ShopReductor {

  override def createShop(businessName: String, name: String, activityId: Int, stratumId: Int, address: String,
                          phoneNumber: String, email: String, website: String, shopTypeId: Int,
                          position: (Long, Long)): Shop = {
    shopRepository.createShop(businessName, name, activityId, stratumId, address, phoneNumber, email, website,
      shopTypeId, position)
  }

  override def all(): List[Shop] = shopRepository.getAll()
}
