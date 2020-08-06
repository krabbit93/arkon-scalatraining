package training.modules.shops

import cats.implicits.catsSyntaxApplicativeId
import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator
import training.domain.ShopType

class ShopTypeRepository {
  def find(shopTypeId: Int): ConnectionIO[Option[ShopType]] = {
    sql"""
          select id, name from shop_type where id = ${shopTypeId};
    """.query[ShopType].option
  }

  def findByName(shopTypeName: String): ConnectionIO[Option[ShopType]] =
    sql"""
         |select id, name from shop_type where name = $shopTypeName
         |""".stripMargin
      .query[ShopType]
      .option

  def create(shopTypeName: String): ConnectionIO[Int] = {
    sql"""
         |INSERT INTO shop_type(id, name) VALUES (
         |(SELECT COALESCE(MAX(id), 0) + 1 FROM shop_type)
         |, $shopTypeName)
         |""".stripMargin.update
      .withUniqueGeneratedKeys[Int]("id")
  }

  def getIdOrCreate(shopTypeName: Option[String]): ConnectionIO[Option[Int]] =
    shopTypeName match {
      case None => (None: Option[Int]).pure[ConnectionIO]
      case Some(value) =>
        (for {
          shopType <- findByName(value)
          shopTypeId <- (shopType match {
              case None        => create(value)
              case Some(value) => value.id.pure[ConnectionIO]
            })
        } yield Some(shopTypeId))
    }
}
