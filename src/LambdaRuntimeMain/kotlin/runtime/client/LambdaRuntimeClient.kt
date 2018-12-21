package runtime.client

import httprekwest.HttpRekwest

class LambdaRuntimeClient {
    private val rekwest = HttpRekwest()

    fun getExample() {
        val resp = rekwest.get("http://example.com")
        println(resp)
    }
}