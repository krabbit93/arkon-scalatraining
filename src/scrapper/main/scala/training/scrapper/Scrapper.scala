package training.scrapper

import cats.effect.IO
import com.typesafe.scalalogging.Logger
import training.scrapper.config.dependencies._
import training.scrapper.modules.apiclient.ApiClient
import training.scrapper.modules.shared.{Query, ScrapperError}
import training.scrapper.modules.webcollector.WebCollector

object Scrapper extends App {
  private val logger = Logger(Scrapper.getClass)
  logger.info("Initialize project scrapper")

  def run()(implicit collector: WebCollector, client: ApiClient): Unit =
    collector(Query.fromConfig())
      .flatMap({
        case Right(value) => client(value)
        case Left(error)  => IO(Left(error))
      })
      .runAsync(cb => {
        cb match {
          case Left(exception: Throwable) =>
            logger.error("Unexpected error ocurred: ", exception)
          case Right(result: Either[ScrapperError, Unit]) =>
            result match {
              case Right(_) =>
                logger.info("Request successfully")
              case Left(error: ScrapperError) =>
                logger.error(s"A error occurred: ${error.message}, cause: ", error.e.orNull)
            }
        }
        IO { system.terminate() }
      })
      .unsafeRunSync()

  run()
}
