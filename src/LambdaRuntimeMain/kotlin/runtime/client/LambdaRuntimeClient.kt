package runtime.client

import httprekwest.HttpRekwest
import runtime.client.exception.BadRequestException
import runtime.handler.InvocationRequest

object LambdaRuntimeClient {
    private val rekwest = HttpRekwest()

    fun run(handler: (InvocationRequest) -> String) {
        while(true) {
            val invocationHttpRequest = rekwest.get("http://${EnvironmentConfiguration.lambdaRuntimeApi}/2018-06-01/runtime/invocation/next")
            try {
                println("Invocation status code: ${invocationHttpRequest.statusCode}")
                println("Invocation headers: ${invocationHttpRequest.headers}")
                println("Invocation body: ${invocationHttpRequest.body}")
                if (!invocationHttpRequest.headers.containsKey("Lambda-Runtime-Aws-Request-Id")) {
                    continue
                }
                val invocationRequest = RequestMapper.mapRequest(invocationHttpRequest)
                println("Invocation Request: $invocationRequest")
                val handlerResponse: String = try {
                    handler(invocationRequest)
                }
                catch (ex: Exception) {
                    val errorPayload = "{" +
                            "\"errorMessage\": \"Failed to load function.\"," +
                            "\"errorType\": \"${ex::class.simpleName}\"" +
                            "}"
                    rekwest.post(
                            "http://${EnvironmentConfiguration.lambdaRuntimeApi}/2018-06-01/runtime/invocation/init/error",
                            mapOf("REQUEST_ID" to invocationRequest.requestId),
                            errorPayload
                    )
                    continue
                }
                println("Handler response: $handlerResponse")
                val responseHeaders = mutableMapOf("REQUEST_ID" to invocationRequest.requestId)
                if (!invocationRequest.xrayTraceId.isNullOrEmpty()) {
                    responseHeaders["_X_AMZN_TRACE_ID"] = invocationRequest.xrayTraceId!!
                }

                val postResponse = rekwest.post(
                        "http://${EnvironmentConfiguration.lambdaRuntimeApi}/2018-06-01/runtime/invocation/${invocationRequest.requestId}/response",
                        responseHeaders,
                        handlerResponse
                )

                println("Response status code: ${postResponse.statusCode}")
                println("Response headers: ${postResponse.headers}")
                println("Response body: ${postResponse.body}")
            }
            catch (ex: BadRequestException) {
                val errorPayload = "{" +
                        "\"errorMessage\": \"${ex.message}\"," +
                        "\"errorType\": \"${ex::class.simpleName}\"" +
                        "}"
                rekwest.post(
                        "http://${EnvironmentConfiguration.lambdaRuntimeApi}/2018-06-01/runtime/invocation/${ex.requestId}",
                        mapOf("REQUEST_ID" to ex.requestId),
                        errorPayload
                )
            }
        }
    }
}