package training.modules.shops

import doobie.implicits.{toSqlInterpolator, _}

class ShopTypeRepository {
  def find(shopTypeId: Int): Option[ShopType] = {
    sql"""
          select id, name from shop_type where id = ${shopTypeId};
    """.query[ShopType].option.transact(DataAccess.xa).unsafeRunSync()
  }
}
