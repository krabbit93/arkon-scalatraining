package training.entrypoint

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, OK}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import sangria.execution._
import sangria.marshalling.circe._
import sangria.parser.QueryParser
import training.GraphqlRequest
import training.config.SchemaDefinition
import training.modules.shops.{CommercialActivityRepository, ShopRepository, ShopTypeRepository, StratumRepository}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object Processor {
  val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case (m, e: Throwable) ⇒ HandledException(e.getMessage)
  }

  def apply(
      json: GraphqlRequest
  )(implicit
      executionContext: ExecutionContextExecutor,
      system: ActorSystem,
      shopReductor: ShopReductor
  ): StandardRoute = {
    val log = Logging(system.eventStream, "processor")

    QueryParser.parse(json.query) match {

      case Success(document) =>
        complete(
          Executor
            .execute(
              SchemaDefinition.schema,
              document,
              shopReductor,
              variables = json.variables,
              operationName = json.operationName,
              exceptionHandler = exceptionHandler
            )
            .map(res => OK -> HttpEntity(ContentTypes.`application/json`, res.toString))
            .recover {
              case error: QueryAnalysisError ⇒
                BadRequest -> HttpEntity(ContentTypes.`application/json`, error.resolveError.toString)
              case error: ErrorWithResolver ⇒
                InternalServerError -> HttpEntity(ContentTypes.`application/json`, error.resolveError.toString)
              case error: Throwable =>
                InternalServerError -> HttpEntity(ContentTypes.`application/json`, error.getMessage)
            }
        )

      case Failure(error) =>
        log.error("Error occurred: {}", error)
        complete(BadRequest, "Incorrect Format")

    }
  }
}
