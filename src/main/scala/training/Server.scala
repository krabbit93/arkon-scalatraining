package training

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import io.circe.parser._
import training.Marshallers._

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import scala.util.{Failure, Success}

case class GraphqlRequest(query: String, operationName: Option[String], variables: Option[Map[String, String]])

object Server extends App {
  implicit val system: ActorSystem = ActorSystem("sangria-server")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  val log = Logging(system.eventStream, "sangria-server")
  val host = "0.0.0.0"
  val port = 8080

  val route: Route = post {
    path("api") {
      entity(as[String]) { jsonString => {
        decode[GraphqlRequest](jsonString) match {
          case Left(failure) => {
            log.error("Error in reading: {}", failure)
            complete(BadRequest -> HttpEntity(ContentTypes.`application/json`, "Error in request body"))
          }
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

  val bindingFuture = Http().bindAndHandle(route, host, port)

  bindingFuture.onComplete {
    case Success(serverBinding) =>
      log.info(s"Server bound to ${serverBinding.localAddress}")

    case Failure(ex) =>
      log.error(ex, "Failed to bind to {}:{}!", host, port)
      system.terminate()
  }

  log.info("Press enter key to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
