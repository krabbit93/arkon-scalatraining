package training.scrapper.modules.webcollector

import cats.effect.IO
import training.domain.ShopRaw
import training.scrapper.modules.shared.{Query, ScrapperError}

trait WebCollector {
  def apply(query: Query): IO[Either[ScrapperError, Seq[ShopRaw]]]
}
