package runtime.handler

class TestHandler {
    fun handle(payload: InvocationRequest): String {
        return payload.toString()
    }
}