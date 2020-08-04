package training.modules.shops

import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator

class StratumRepository {
  def find(stratumId: Int): ConnectionIO[Option[Stratum]] =
    sql"""
        select id, name from stratum where id = ${stratumId}
       """
      .query[Stratum]
      .option
}
