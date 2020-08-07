package training.scrapper.modules.apiclient

import io.circe.Json

case class GraphqlQuery(
    query: String,
    operationName: String,
    variables: Json
)
