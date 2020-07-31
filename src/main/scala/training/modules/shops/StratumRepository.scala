package training.modules.shops

import doobie.implicits.{toSqlInterpolator, _}

class StratumRepository {
  def find(stratumId: Int): Stratum =
    sql"""
        select id, name from stratum where id = ${stratumId}
       """
      .query[Stratum]
      .option
      .transact(DataAccess.xa)
      .unsafeRunSync() match {
      case Some(value) => value
      case None        => throw new IllegalStateException
    }
}
