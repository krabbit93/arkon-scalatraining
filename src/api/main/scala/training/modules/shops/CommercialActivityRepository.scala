package training.modules.shops

import cats.implicits.catsSyntaxApplicativeId
import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator
import training.domain.CommercialActivity

class CommercialActivityRepository {

  def find(activityId: Int): ConnectionIO[Option[CommercialActivity]] =
    sql"""
         |SELECT id, name FROM comercial_activity WHERE id = $activityId
         |""".stripMargin
      .query[CommercialActivity]
      .option

  def findByName(activityName: String): ConnectionIO[Option[CommercialActivity]] =
    sql"""
         |SELECT id, name FROM comercial_activity WHERE name = $activityName
         |""".stripMargin
      .query[CommercialActivity]
      .option

  def create(activityName: String): ConnectionIO[Int] = {
    sql"""
         |INSERT INTO comercial_activity(id, name) VALUES (
         |(SELECT COALESCE(MAX(id), 0) + 1 FROM comercial_activity)
         |, $activityName)
         |""".stripMargin.update
      .withUniqueGeneratedKeys[Int]("id")
  }

  def getIdOrCreate(activityName: Option[String]): ConnectionIO[Option[Int]] =
    activityName match {
      case None => (None: Option[Int]).pure[ConnectionIO]
      case Some(value) =>
        for {
          activity <- findByName(value)
          activityId <- activity match {
            case None        => create(value)
            case Some(value) => value.id.pure[ConnectionIO]
          }
        } yield Some(activityId)
    }
}
