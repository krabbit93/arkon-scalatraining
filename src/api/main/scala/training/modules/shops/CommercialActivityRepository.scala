package training.modules.shops

import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator
import training.domain.CommercialActivity

class CommercialActivityRepository {

  def find(activityId: Int): ConnectionIO[Option[CommercialActivity]] =
    sql"""
        select id, name from comercial_activity where id = ${activityId};
     """
      .query[CommercialActivity]
      .option
}
