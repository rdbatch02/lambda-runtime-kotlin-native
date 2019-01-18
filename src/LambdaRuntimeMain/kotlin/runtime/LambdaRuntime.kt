package runtime

import httprekwest.HttpRekwest

//fun main(args: Array<String>) = LambdaRuntimeClient.run {
//    "{\"body:\": \"Hello Kotlin Native\"}"
//}

fun main(args: Array<String>) {
    val rekwest = HttpRekwest()
    println(rekwest.post("http://echo.jpillora.com", mapOf("content-type" to "text/plain"), "{\n" +
            "    \"testBody\": \"test\"\n" +
            "}").body)
}