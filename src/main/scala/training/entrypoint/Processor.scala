package training.entrypoint

import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, OK}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.complete
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.InputUnmarshaller
import sangria.marshalling.circe._
import sangria.parser.QueryParser
import training.Server.log
import training.{GraphqlRequest, SchemaDefinition, ShopRepository}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object Processor {
  def apply(json: GraphqlRequest)(implicit executionContext: ExecutionContextExecutor) = {
    QueryParser.parse(json.query) match {
      case Success(document) => {
        complete(Executor.execute(
          SchemaDefinition.schema, document, new GraphqlShopReductor(new ShopRepository),
          variables = InputUnmarshaller.emptyMapVars,
          operationName = json.operationName
        ).map(res => OK -> HttpEntity(ContentTypes.`application/json`, res.toString)).recover {
          case error: QueryAnalysisError ⇒ BadRequest -> HttpEntity(ContentTypes.`application/json`, error.resolveError.toString)
          case error: ErrorWithResolver ⇒ InternalServerError -> HttpEntity(ContentTypes.`application/json`, error.resolveError.toString)
        }
        )
      }
      case Failure(error) => {
        log.error("Error ocurred: {}", error)
        complete(BadRequest, "Incorrect Format")
      }
    }
  }
}
