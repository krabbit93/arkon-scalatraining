package training.scrapper.modules.shared

case class ScrapperError(message: String, e: Option[Exception] = None)
