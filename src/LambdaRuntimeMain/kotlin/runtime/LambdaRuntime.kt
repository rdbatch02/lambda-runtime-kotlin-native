package runtime

import runtime.client.LambdaRuntimeClient

fun main(args: Array<String>) = LambdaRuntimeClient.run {
    "{\"body:\": \"Hello, Kotlin Native!\"}"
}