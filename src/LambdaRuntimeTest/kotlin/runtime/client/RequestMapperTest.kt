package runtime.client

import httprekwest.HttpResponse
import runtime.handler.InvocationRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class RequestMapperTest {
    @Test
    fun testMapsAWSLambdaRequest() {
        val lambdaRequest = HttpResponse("200", mapOf(
            "Lambda-Runtime-Aws-Request-Id" to "request-id",
            "Lambda-Runtime-Trace-Id" to "trace-id",
            "Lambda-Runtime-Client-Context" to "client-context",
            "Lambda-Runtime-Cognito-Identity" to "cognito-identity",
            "Lambda-Runtime-Invoked-Function-Arn" to "function-arn",
            "Lambda-Runtime-Deadline-Ms" to "123456"
        ), "testPayload")
        val invocationRequest = RequestMapper.mapRequest(lambdaRequest)
        val expectedInvocationRequest = InvocationRequest(
            payload = "testPayload",
            requestId = "request-id",
            xrayTraceId = "trace-id",
            clientContext = "client-context",
            cognitoIdentity = "cognito-identity",
            functionArn = "function-arn",
            deadline = 123456L
        )

        assertEquals(expectedInvocationRequest, invocationRequest)
    }
}