package com.batchofcode.lambdaruntime.client

import com.batchofcode.lambdaruntime.client.exception.BadRequestException
import com.batchofcode.lambdaruntime.handler.InvocationRequest
import com.batchofcode.lambdaruntime.util.fromMap
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.response.HttpResponse
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import kotlinx.io.core.use
import kotlin.collections.set

class CommonAwsRuntimeClient(private val client: HttpClient) {
    suspend fun run(handler: (InvocationRequest) -> String) {
        client.use { client ->
                processRequests(client, handler)
//            while (true) {
//            }
        }
    }

    suspend fun processRequests(client: HttpClient, handler: (InvocationRequest) -> String) {
        println("Requesting invocation from http://${EnvironmentConfiguration.lambdaRuntimeApi}/2018-06-01/runtime/invocation/next")
        val invocationHttpRequest =
            client.request<HttpResponse>{
                url("http://${EnvironmentConfiguration.lambdaRuntimeApi}/2018-06-01/runtime/invocation/next")
                method = HttpMethod.Get
            }
        try {
            if (!invocationHttpRequest.headers.contains("Lambda-Runtime-Aws-Request-Id")) {
                return
            }
            val invocationRequest = RequestMapper.mapRequest(invocationHttpRequest)
            val responseHeaders =
                mutableMapOf("REQUEST_ID" to invocationRequest.requestId)
            if (!invocationRequest.xrayTraceId.isNullOrEmpty()) {
                responseHeaders["_X_AMZN_TRACE_ID"] = invocationRequest.xrayTraceId
            }
            val handlerResponse: String = try {
                handler(invocationRequest) // Invoke handler
            } catch (ex: Exception) {
                val errorPayload = "{" +
                        "\"errorType\": \"${ex::class.simpleName}\"," +
                        "\"errorMessage\": \"${ex.message}.\"" +
                        "}"
                println(ex)
                client.post<Unit> {
                    url("http://${EnvironmentConfiguration.lambdaRuntimeApi}/2018-06-01/runtime/invocation/${invocationRequest.requestId}/error")
                    headers.fromMap(responseHeaders)
                    body = TextContent(errorPayload, ContentType.Application.Json)
                }
                return
            }
            client.post<Unit> {
                url("http://${EnvironmentConfiguration.lambdaRuntimeApi}/2018-06-01/runtime/invocation/${invocationRequest.requestId}/response")
                headers.fromMap(responseHeaders)
                body = TextContent(handlerResponse, ContentType.Text.Any)
            }
        } catch (ex: BadRequestException) {
            val errorPayload = "{" +
                    "\"errorMessage\": \"${ex.message}\"," +
                    "\"errorType\": \"${ex::class.simpleName}\"" +
                    "}"
            client.post<Unit> {
                url("http://${EnvironmentConfiguration.lambdaRuntimeApi}/2018-06-01/runtime/init/error")
                headers.append("REQUEST_ID", ex.requestId)
                body = TextContent(errorPayload, ContentType.Application.Json)
            }
        }
    }
}