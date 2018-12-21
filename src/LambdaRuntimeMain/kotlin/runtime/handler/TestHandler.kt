package runtime.handler

class TestHandler: LambdaHandler {
    override fun handle(payload: InvocationRequest): String {
        return payload.toString()
    }
}