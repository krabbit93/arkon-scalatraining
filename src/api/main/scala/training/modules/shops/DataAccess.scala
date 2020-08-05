package training.modules.shops

import akka.dispatch.ExecutionContexts
import cats.effect.{ContextShift, IO}
import com.typesafe.config.{Config, ConfigFactory}
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux

object DataAccess {
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.global())

  val databaseConf: Config = ConfigFactory.load("database")

  val xa: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    s"jdbc:postgresql://${databaseConf.getString("host")}:${databaseConf.getInt("port")}/${databaseConf.getString("dbname")}",
    databaseConf.getString("username"),
    databaseConf.getString("password")
  )
}
