package training

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.circe.parser.decode
import training.config.Marshallers._
import training.entrypoint.Processor

import scala.concurrent.ExecutionContextExecutor

object Routes {
  def all()(implicit executionContext: ExecutionContextExecutor, system: ActorSystem): Route = {
    val log = Logging(system.eventStream, "routes-resolver")

    post {
      path("api") {
        entity(as[String]) { jsonString =>
          {
            decode[GraphqlRequest](jsonString) match {
              case Left(failure) =>
                log.error("Error in reading: {}", failure)
                complete(BadRequest -> HttpEntity(ContentTypes.`application/json`, "Error in request body"))

              case Right(json) => Processor(json)
            }
          }
        }
      }
    } ~ get {
      path("graphiql") {
        getFromResource("graphiql.html")
      }
    }
  }
}
