package httprekwest

interface HttpRekwest {
    fun get(url: String, headers: Map<String, String>? = null): HttpResponse
    fun post(url: String, headers: Map<String, String>? = null, payload: String): HttpResponse
}