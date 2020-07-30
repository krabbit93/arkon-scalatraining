package training.entrypoint

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, OK}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.ScalaInput
import sangria.marshalling.circe._
import sangria.parser.QueryParser
import sangria.util.tag
import training.GraphqlRequest
import training.config.SchemaDefinition
import training.modules.shops.ShopRepository

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object Processor {
  def apply(json: GraphqlRequest)(implicit executionContext: ExecutionContextExecutor, system: ActorSystem): StandardRoute = {
    val log = Logging(system.eventStream, "processor")

    QueryParser.parse(json.query) match {

      case Success(document) =>
        complete(Executor.execute(
          SchemaDefinition.schema, document, new GraphqlShopReductor(new ShopRepository),
          variables = json.variables,
          operationName = json.operationName
        ).map(res => OK -> HttpEntity(ContentTypes.`application/json`, res.toString)).recover {
          case error: QueryAnalysisError ⇒ BadRequest -> HttpEntity(ContentTypes.`application/json`, error.resolveError.toString)
          case error: ErrorWithResolver ⇒ InternalServerError -> HttpEntity(ContentTypes.`application/json`, error.resolveError.toString)
        }
        )

      case Failure(error) =>
        log.error("Error occurred: {}", error)
        complete(BadRequest, "Incorrect Format")

    }
  }
}
