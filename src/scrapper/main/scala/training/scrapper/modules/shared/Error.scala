package training.scrapper.modules.shared

case class Error(message: String, e: Option[Exception] = None)
