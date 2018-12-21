package runtime

import runtime.client.EnvironmentConfiguration
import runtime.client.LambdaRuntimeClient
import runtime.handler.TestHandler

fun hello(): String = "Hello, Kotlin/Native!"

fun main(args: Array<String>) {
    val config = EnvironmentConfiguration
    val client = LambdaRuntimeClient(TestHandler())
    println(hello())
    client.run()
}