package runtime.handler

interface LambdaHandler {
    fun handle(payload: InvocationRequest): String
}