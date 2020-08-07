package training.config

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import training.entrypoint.GraphqlShopReductor
import training.modules.shops.{CommercialActivityRepository, ShopRepository, ShopTypeRepository, StratumRepository}

import scala.concurrent.ExecutionContextExecutor

object dependencies {

  implicit val system: ActorSystem                        = ActorSystem("sangria-server")
  implicit val materializer: ActorMaterializer            = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  implicit val shopRepository: ShopRepository                             = new ShopRepository()
  implicit val ShopTypeRepository: ShopTypeRepository                     = new ShopTypeRepository()
  implicit val StratumRepository: StratumRepository                       = new StratumRepository()
  implicit val CommercialActivityRepository: CommercialActivityRepository = new CommercialActivityRepository()

  implicit val shopReductor: GraphqlShopReductor = new GraphqlShopReductor()

}
