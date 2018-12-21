package runtime

import runtime.client.LambdaRuntimeClient

fun hello(): String = "Hello, Kotlin/Native!"

fun main(args: Array<String>) {
    val config = EnvironmentConfiguration
    val client = LambdaRuntimeClient()
    println(hello())
    client.getExample()
}