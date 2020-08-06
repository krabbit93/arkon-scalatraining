package training.scrapper.modules.shared

import com.typesafe.config.{Config, ConfigFactory}

case class Query(term: String, lat: Double, long: Double, quantity: Int) {

  def build(): String =
    Query.clientConfig
      .getString("serverUrl")
      .format(
        term,
        lat,
        long,
        quantity
      )
}

object Query {
  private val clientConfig = ConfigFactory.load("client")

  def fromConfig(): Query = {
    Query(
      clientConfig.getString("term"),
      clientConfig.getDouble("lat"),
      clientConfig.getDouble("long"),
      clientConfig.getInt("quantity")
    )
  }
}
