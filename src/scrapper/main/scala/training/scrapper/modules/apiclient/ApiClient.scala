package training.scrapper.modules.apiclient

import cats.effect.IO
import training.domain.ShopRaw
import training.scrapper.modules.shared.ScrapperError

trait ApiClient {
  def apply(shops: Seq[ShopRaw]): IO[Either[ScrapperError, Unit]]
}
