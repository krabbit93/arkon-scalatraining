package training.modules.shops

import cats.implicits.catsSyntaxApplicativeId
import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator
import training.domain.Stratum

class StratumRepository {
  def find(stratumId: Int): ConnectionIO[Option[Stratum]] =
    sql"""
        select id, name from stratum where id = ${stratumId}
       """
      .query[Stratum]
      .option

  def findByName(stratumName: String): ConnectionIO[Option[Stratum]] =
    sql"""
         |select id, name from stratum where name = $stratumName
         |""".stripMargin
      .query[Stratum]
      .option

  def create(stratumName: String): ConnectionIO[Int] = {
    sql"""
         |INSERT INTO stratum(id, name) VALUES (
         |(SELECT COALESCE(MAX(id), 0) + 1 FROM stratum)
         |, $stratumName)
         |""".stripMargin.update
      .withUniqueGeneratedKeys[Int]("id")
  }

  def getIdOrCreate(stratumName: Option[String]): ConnectionIO[Option[Int]] =
    stratumName match {
      case None => (None: Option[Int]).pure[ConnectionIO]
      case Some(value) =>
        (for {
          stratum <- findByName(value)
          stratumId <- (stratum match {
              case None        => create(value)
              case Some(value) => value.id.pure[ConnectionIO]
            })
        } yield Some(stratumId))
    }
}
