package runtime.client

import runtime.handler.InvocationRequest

actual class LambdaRuntimeClient {
    actual fun run(handler: (InvocationRequest) -> String) {
        println("WARNING! Runtime not supported on MacOS Platform.")
    }
}