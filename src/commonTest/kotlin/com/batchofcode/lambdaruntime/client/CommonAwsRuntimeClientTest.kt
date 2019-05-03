package com.batchofcode.lambdaruntime.client

import com.batchofcode.lambdaruntime.runTest
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.Headers
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import io.ktor.util.InternalAPI
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@InternalAPI
class CommonAwsRuntimeClientTest {
    @Test
    fun `should handle a invocation request`() {
        val httpMockEngine = MockEngine { request -> // this: HttpRequest, call: HttpClientCall
            if (request.method != HttpMethod.Get) {
                return@MockEngine respondOk()
            }
            when (request.url.fullPath) {
                "/2018-06-01/runtime/invocation/next" -> {
                    respond(
                        content = "test",
                        status = HttpStatusCode.OK,
                        headers = Headers.build {
                            append("Lambda-Runtime-Aws-Request-Id", "1")
                            append("Lambda-Runtime-Invoked-Function-Arn", "aws:arn:unit:test")
                            append("Lambda-Runtime-Deadline-Ms", "1000")
                        }
                    )
                }
                else -> {
                    error("Unhandled URL ${request.url.fullPath}")
                }
            }
        }
        val mockClient = HttpClient(httpMockEngine)
        val runtimeClient = CommonAwsRuntimeClient(mockClient)
        var handled = false
        runTest { runtimeClient.processRequests(mockClient) {
            handled = true
            ""
        }
        }
        assertTrue(handled)
    }

    @Test
    fun `should not handle a request when missing Lambda-Runtime-Aws-Request-Id header`() {
        val httpMockEngine = MockEngine { request -> // this: HttpRequest, call: HttpClientCall
            when (request.url.fullPath) {
                "/2018-06-01/runtime/invocation/next" -> {
                    respond("")
                }
                else -> {
                    error("Unhandled URL ${request.url.fullPath}")
                }
            }
        }

        val mockClient = HttpClient(httpMockEngine)
        val runtimeClient = CommonAwsRuntimeClient(mockClient)
        var handled = false
        runTest { runtimeClient.processRequests(mockClient) {
                handled = true
                ""
            }
        }
        assertFalse(handled)
    }

    @Test
    fun `should post to error endpoint when handler throws an exception`() {
        var errorCalled = false
        val httpMockEngine = MockEngine { request -> // this: HttpRequest, call: HttpClientCall
            when (request.url.fullPath) {
                "/2018-06-01/runtime/invocation/next" -> {
                    respond(
                        content = "test",
                        status = HttpStatusCode.OK,
                        headers = Headers.build {
                            append("Lambda-Runtime-Aws-Request-Id", "1")
                            append("Lambda-Runtime-Invoked-Function-Arn", "aws:arn:unit:test")
                            append("Lambda-Runtime-Deadline-Ms", "1000")
                        }
                    )
                }
                "/2018-06-01/runtime/invocation/1/error" -> {
                    errorCalled = true
                    respond("")
                }
                else -> {
                    error("Unhandled URL ${request.url.fullPath}")
                }
            }
        }
        val mockClient = HttpClient(httpMockEngine)
        val runtimeClient = CommonAwsRuntimeClient(mockClient)
        runTest {
            runtimeClient.processRequests(mockClient) {
                throw Exception()
            }
        }
        assertTrue(errorCalled)
    }
}