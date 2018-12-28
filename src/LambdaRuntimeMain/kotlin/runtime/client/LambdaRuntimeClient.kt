package runtime.client

import httprekwest.HttpRekwest
import runtime.client.exception.BadRequestException
import runtime.handler.LambdaHandler

class LambdaRuntimeClient(val handler: LambdaHandler) {
    private val rekwest = HttpRekwest()

    fun run() {
        while(true) {
            val invocationHttpRequest = rekwest.get("http://${EnvironmentConfiguration.lambdaRuntimeApi}/2018-06-01/runtime/invocation/next")
            try {
                val invocationRequest = RequestMapper.mapRequest(invocationHttpRequest)
                val handlerResponse: String = try {
                    handler.handle(invocationRequest)
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
                val responseHeaders = mutableMapOf("REQUEST_ID" to invocationRequest.requestId)
                if (!invocationRequest.xrayTraceId.isNullOrEmpty()) {
                    responseHeaders["_X_AMZN_TRACE_ID"] = invocationRequest.xrayTraceId!!
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