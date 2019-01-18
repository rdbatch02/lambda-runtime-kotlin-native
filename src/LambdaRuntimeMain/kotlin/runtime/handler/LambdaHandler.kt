package runtime.handler

interface LambdaHandler {
    fun handle(handler: (InvocationRequest) -> String): String
}