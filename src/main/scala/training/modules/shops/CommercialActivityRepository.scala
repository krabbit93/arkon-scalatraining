package training.modules.shops

import doobie.implicits.{toSqlInterpolator, _}

class CommercialActivityRepository {

  def find(activityId: Int): CommercialActivity =
    sql"""
        select id, name from comercial_activity where id = ${activityId};
     """
      .query[CommercialActivity]
      .option
      .transact(DataAccess.xa)
      .unsafeRunSync() match {
      case Some(value) => value
      case None        => throw new IllegalStateException
    }
}
