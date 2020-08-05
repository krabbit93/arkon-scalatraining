package training.scrapper.modules.webcollector

import training.domain.Shop
import training.scrapper.modules.shared.Error

trait WebCollector {
  def invoke(url: String): Either[Error, Seq[Shop]]
}
