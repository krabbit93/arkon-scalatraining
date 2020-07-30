package training.entrypoint

import training.modules.shops.{Position, Shop}

trait ShopReductor {

  /**
    * Create a shop with parameters obtained of mutation
    */
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
  ): Shop

  /**
    * Retrive list of all shop registered
    */
  def all(): List[Shop]
}
