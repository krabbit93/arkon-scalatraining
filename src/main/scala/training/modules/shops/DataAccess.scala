package training.modules.shops

import akka.dispatch.ExecutionContexts
import cats.effect.{ContextShift, IO}
import doobie.util.transactor.Transactor

object DataAccess {
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.global())

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5432/shops",
    "admin",
    "admin123"
  )
}
