package training.modules.shops

import doobie.implicits.{toSqlInterpolator, _}

class CommercialActivityRepository {

  def find(activityId: Int): Option[CommercialActivity] =
    sql"""
        select id, name from comercial_activity where id = ${activityId};
     """
      .query[CommercialActivity]
      .option
      .transact(DataAccess.xa)
      .unsafeRunSync()
}
