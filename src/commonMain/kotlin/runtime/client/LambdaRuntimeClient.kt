package runtime.client

import runtime.handler.InvocationRequest

expect class LambdaRuntimeClient {
    fun run(handler: (InvocationRequest) -> String)
}