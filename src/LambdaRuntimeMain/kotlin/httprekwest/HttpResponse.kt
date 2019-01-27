package httprekwest

data class HttpResponse(
    val statusCode: String = "200",
    val headers: Map<String, String>,
    val body: String
)