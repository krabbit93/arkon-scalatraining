package training.scrapper.modules.shared

import training.BaseSpec

class QuerySpec extends BaseSpec {
  private val startUrl = "https://www.inegi.org.mx/app/api/denue/v1/consulta/Buscar/alias/1.0,1.0/1/"

  "Query" should "build url as" in {
    Query(
      term = "alias",
      lat = 1.0,
      long = 1.0,
      quantity = 1
    ).build() should startWith(startUrl)
  }

}
