package training.modules.shops

import doobie.implicits.{toSqlInterpolator, _}

class StratumRepository {
  def find(stratumId: Int): Option[Stratum] =
    sql"""
        select id, name from stratum where id = ${stratumId}
       """
      .query[Stratum]
      .option
      .transact(DataAccess.xa)
      .unsafeRunSync()
}
