package httprekwest

import kurl.KUrl

class KUrlHttpRekwest(private val kurl: KUrl = KUrl()): HttpRekwest {
    override fun get(url: String, headers: Map<String, String>?): HttpResponse {
        val headerList: MutableList<String> = mutableListOf()
        var body = ""

        kurl.get(url, headers, {body += it}, {headerList.add(it)})

        return HttpResponse(
                headers = headerList.filter { it.contains(":") }.associate { splitHeader(it) },
                statusCode = headerList.firstOrNull { !it.contains(":") }?.let { Regex("[\\d]{3}").find(it)?.value } ?: "200",
                body = body
        )
    }

    override fun post(url: String, headers: Map<String, String>?, payload: String): HttpResponse {
        val headerList: MutableList<String> = mutableListOf()
        var body = ""

        kurl.post(url, headers, payload,  {body += it}, {headerList.add(it)})

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