package training.entrypoint

import training.modules.shops.Shop

trait ShopReductor {
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
                  position: (Long, Long)
                ): Shop

  def all(): List[Shop]
}
