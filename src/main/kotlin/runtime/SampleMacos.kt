package runtime

import libcurl.*

fun hello(): String = "Hello, Kotlin/Native!"

fun main(args: Array<String>) {
    val curl = curl_easy_init()
    println(hello())
}