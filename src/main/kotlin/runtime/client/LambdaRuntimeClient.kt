package runtime

import kurl.*

class LambdaRuntimeClient {
    val kurl = KUrl()

    fun getExample() {
        kurl.get("http://example.com", null, {println(it)}, null)
    }
}