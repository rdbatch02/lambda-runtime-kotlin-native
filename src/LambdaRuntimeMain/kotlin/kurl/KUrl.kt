package kurl

import kotlinx.cinterop.*
import libcurl.*
import platform.posix.size_t
import kotlin.native.concurrent.SharedImmutable

class KUrlError(message: String) : Error(message)

typealias HttpHandler = (String) -> Unit

private fun CPointer<ByteVar>.toKString(length: Int) = this.readBytes(length).stringFromUtf8()

@SharedImmutable
private val curlConfigReturn = curl_global_init(CURL_GLOBAL_ALL)

class KUrl(val cookies: String? = null) {
    fun escape(curl: COpaquePointer?, string: String) =
      curl_easy_escape(curl, string, 0) ?. let {
        val result = it.toKString()
        curl_free(it)
        result
      } ?: ""

    fun get(url: String, options: Map<String, String>?, onData: HttpHandler, onHeader: HttpHandler?) {
        makeRequest(url, options, null, RequestType.GET, onData, onHeader)
    }

    fun post(url: String, options: Map<String, String>?, payload: String, onData: HttpHandler, onHeader: HttpHandler?) {
        makeRequest(url, options, payload, RequestType.POST, onData, onHeader)
    }

    private fun makeRequest(url: String, options: Map<String, String>?, payload: String?, requestType: RequestType, onData: HttpHandler, onHeader: HttpHandler?) {
        var curl = curl_easy_init()
        var headerStruct: CValuesRef<curl_slist>? = null
        options?.forEach {
            headerStruct = curl_slist_append(headerStruct, "${it.key}: ${it.value}")
        }
        curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headerStruct)
        curl_easy_setopt(curl, CURLOPT_URL, url.cstr)
        if (cookies != null) {
            curl_easy_setopt(curl, CURLOPT_COOKIEFILE, cookies)
            curl_easy_setopt(curl, CURLOPT_COOKIELIST, "RELOAD")
        }
        if (requestType == RequestType.POST) {
            curl_easy_setopt(curl, CURLOPT_COPYPOSTFIELDS, payload)
        }
        val stables = mutableListOf<StableRef<Any>>()
        val result = try {
            if (onHeader != null) {
                curl_easy_setopt(curl, CURLOPT_HEADERFUNCTION, staticCFunction {
                    buffer: CPointer<ByteVar>?, size: size_t, nitems: size_t, userdata: COpaquePointer? ->

                    if (buffer == null) return@staticCFunction 0.toLong()
                    val handler = userdata!!.asStableRef<HttpHandler>().get()
                    handler(buffer.toKString((size * nitems).toInt()).trim())
                    return@staticCFunction (size * nitems).toLong()
                })
                val onHeaderStable = StableRef.create(onHeader)
                stables += onHeaderStable
                curl_easy_setopt(curl, CURLOPT_HEADERDATA, onHeaderStable.asCPointer())
            }

            curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, staticCFunction {
                buffer: CPointer<ByteVar>?, size: size_t, nitems: size_t, userdata: COpaquePointer? ->

                if (buffer == null) return@staticCFunction 0.toLong()
                val header = buffer.toKString((size * nitems).toInt())
                val handler = userdata!!.asStableRef<HttpHandler>().get()
                handler(header)
                return@staticCFunction (size * nitems).toLong()
            })
            val onDataStable = StableRef.create(onData)
            stables += onDataStable
            curl_easy_setopt(curl, CURLOPT_WRITEDATA, onDataStable.asCPointer())

            curl_easy_perform(curl)
        } finally {
            stables.forEach {
                it.dispose()
            }
            curl_easy_cleanup(curl)
        }

        if (result != CURLE_OK)
            throw KUrlError("curl_easy_perform() failed with code $result: ${curl_easy_strerror(result)?.toKString() ?: ""}")
    }

    // So that we can use DSL syntax.
    fun fetch(url: String, options: Map<String, String>? = null, onData: HttpHandler) =
            makeRequest(url, options, null, RequestType.GET, onData, null)
}