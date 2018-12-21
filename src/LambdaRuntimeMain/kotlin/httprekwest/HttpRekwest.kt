package httprekwest

import kurl.*

class HttpRekwest(private val kurl: KUrl = KUrl()) {
    fun get(url: String, options: Map<String, String>? = null): HttpResponse {
        val headerList: MutableList<String> = mutableListOf()
        var body = ""

        kurl.get(url, options, {body += it}, {headerList.add(it)})

        return HttpResponse(
                headers = headerList.filter { it.contains(":") }.associate { splitHeader(it) },
                statusCode = headerList.firstOrNull { !it.contains(":") }?.let { Regex("[\\d]{3}").find(it)?.value } ?: "200",
                body = body
        )
    }

    private fun splitHeader(header: String): Pair<String, String> {
        return Pair(header.substringBefore(":").trim(), header.substringAfter(":").trim())
    }
}

data class HttpResponse(
        val statusCode: String = "200",
        val headers: Map<String, String>,
        val body: String
)