package training.scrapper.modules.apiclient
import cats.effect.IO
import com.typesafe.scalalogging.Logger
import training.domain.ShopRaw
import training.scrapper.modules.shared.ScrapperError

final class GraphqlApiClient extends ApiClient {
  private val logger = Logger[GraphqlApiClient]

  override def apply(shops: Seq[ShopRaw]): IO[Either[ScrapperError, Unit]] =
    IO({
      logger.info("Shops processed are: {}", shops)
      Right(())
    })
}
