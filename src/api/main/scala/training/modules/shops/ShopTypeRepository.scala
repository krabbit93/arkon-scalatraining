package training.modules.shops

import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator

class ShopTypeRepository {
  def find(shopTypeId: Int): ConnectionIO[Option[ShopType]] = {
    sql"""
          select id, name from shop_type where id = ${shopTypeId};
    """.query[ShopType].option
  }
}
