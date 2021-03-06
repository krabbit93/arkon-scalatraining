package training

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import scala.util.{Failure, Success}

case class GraphqlRequest(query: String, operationName: Option[String], variables: Option[Map[String, String]])

object Server extends App {
  implicit val system: ActorSystem = ActorSystem("sangria-server")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val log = Logging(system.eventStream, "sangria-server")

  val serverConfig = ConfigFactory.load("server")
  val host = serverConfig.getString("host")
  val port = serverConfig.getInt("port")

  val route = Routes.all()
  val bindingFuture = Http().bindAndHandle(route, host, port)

  bindingFuture.onComplete {
    case Success(serverBinding) =>
      log.info(s"Server bound to ${serverBinding.localAddress}")
      waitKeyForTermination()

    case Failure(ex) =>
      log.error(ex, "Failed to bind to {}:{}!", host, port)
      system.terminate()
  }

  private def waitKeyForTermination() = {
    log.info("Press enter key to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
