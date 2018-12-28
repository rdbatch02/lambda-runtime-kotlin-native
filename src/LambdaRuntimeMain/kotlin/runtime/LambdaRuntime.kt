package runtime

import runtime.client.EnvironmentConfiguration
import runtime.client.LambdaRuntimeClient
import runtime.handler.TestHandler

fun main(args: Array<String>) {
    val config = EnvironmentConfiguration
    val client = LambdaRuntimeClient(TestHandler())
    client.run()
}