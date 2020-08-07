package training.scrapper.config

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import cats.effect.{ContextShift, IO}
import com.typesafe.config.{Config, ConfigFactory}
import training.scrapper.modules.apiclient.{ApiClient, GraphqlApiClient}
import training.scrapper.modules.webcollector.{INEGIWebCollector, WebCollector}

import scala.concurrent.ExecutionContextExecutor

object dependencies {
  val config: Config =
    ConfigFactory.parseString(
      """
        akka.http.host-connection-pool.client.idle-timeout = 120000 millis
        akka.http.host-connection-pool.client.max-retries = 0
      """
    )

  implicit val collector: WebCollector = new INEGIWebCollector
  implicit val client: ApiClient       = new GraphqlApiClient

  implicit val system: ActorSystem             = ActorSystem("client", config)
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val contextShift: ContextShift[IO]             = IO.contextShift(executionContext)
}
