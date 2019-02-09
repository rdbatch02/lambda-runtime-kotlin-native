package runtime.client

import httprekwest.HttpRekwest
import httprekwest.KUrlHttpRekwest
import runtime.client.exception.BadRequestException
import runtime.handler.InvocationRequest

class LambdaRuntimeClient(private val rekwest: HttpRekwest = KUrlHttpRekwest()) {
    fun run(handler: (InvocationRequest) -> String) {
        while(true) {
            val invocationHttpRequest = rekwest.get("http://${EnvironmentConfiguration.lambdaRuntimeApi}/2018-06-01/runtime/invocation/next")
            try {
                if (!invocationHttpRequest.headers.containsKey("Lambda-Runtime-Aws-Request-Id")) {
                    continue
                }
                val invocationRequest = RequestMapper.mapRequest(invocationHttpRequest)
                val handlerResponse: String = try {
                    handler(invocationRequest)
                }
                catch (ex: Exception) {
                    val errorPayload = "{" +
                            "\"errorMessage\": \"Failed to execute function.\"," +
                            "\"errorType\": \"${ex::class.simpleName}\"" +
                            "}"
                    println(ex)
                    rekwest.post(
                            "http://${EnvironmentConfiguration.lambdaRuntimeApi}/2018-06-01/runtime/invocation/init/error",
                            mapOf("REQUEST_ID" to invocationRequest.requestId, "Content-Type" to "application/json"),
                            errorPayload
                    )
                    continue
                }
                val responseHeaders = mutableMapOf("REQUEST_ID" to invocationRequest.requestId, "Content-Type" to "application/json")
                if (!invocationRequest.xrayTraceId.isNullOrEmpty()) {
                    responseHeaders["_X_AMZN_TRACE_ID"] = invocationRequest.xrayTraceId
                }

                rekwest.post(
                        "http://${EnvironmentConfiguration.lambdaRuntimeApi}/2018-06-01/runtime/invocation/${invocationRequest.requestId}/response",
                        responseHeaders,
                        handlerResponse
                )
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