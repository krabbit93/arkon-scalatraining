package training.scrapper

import akka.http.scaladsl.Http
import cats.effect.IO
import cats.implicits.catsSyntaxApplicativeId
import com.typesafe.scalalogging.Logger
import training.scrapper.config.dependencies._
import training.scrapper.modules.shared.{Query, ScrapperError}

object Scrapper extends App {
  private val logger = Logger(Scrapper.getClass)
  logger.info("Initialize project scrapper")

  collector(Query.fromConfig())
    .flatMap({
      case Right(value) => client(value)
      case Left(error)  => Left(error).pure[IO]
    })
    .runAsync(cb => {
      cb match {
        case Left(exception: Throwable) =>
          logger.error("Unexpected error ocurred: ", exception)
        case Right(result: Either[ScrapperError, String]) =>
          result match {
            case Right(str) =>
              logger.info(f"Request successfully:\n$str")
            case Left(error: ScrapperError) =>
              logger.error(f"A error occurred: ${error.message}, cause: ", error.e.orNull)
          }
      }
      IO.fromFuture(Http().shutdownAllConnectionPools().pure[IO])
        .flatMap(_ => {
          system.terminate()
          ().pure[IO]
        })
    })
    .unsafeRunSync()
}
