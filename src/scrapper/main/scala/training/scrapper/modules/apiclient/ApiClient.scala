package training.scrapper.modules.apiclient

import training.domain.Shop

trait ApiClient {
  def call(shops: Seq[Shop]): Either[Error, Unit]
}
