package com.batchofcode.lambdaruntime.client

import com.batchofcode.lambdaruntime.client.exception.BadRequestException
import com.batchofcode.lambdaruntime.client.exception.MissingRequestIdException
import com.batchofcode.lambdaruntime.handler.InvocationRequest
import io.ktor.client.response.HttpResponse
import io.ktor.client.response.readText
import kotlinx.io.charsets.Charsets

object RequestMapper {
    suspend fun mapRequest(req: HttpResponse): InvocationRequest {
        val requestId = req.headers["Lambda-Runtime-Aws-Request-Id"] ?: throw MissingRequestIdException("Missing Lambda-Runtime-Aws-Request-Id")
        return InvocationRequest(
            payload = req.readText(Charsets.UTF_8),
            requestId = requestId,
            xrayTraceId = req.headers["Lambda-Runtime-Trace-Id"],
            clientContext = req.headers["Lambda-Runtime-Client-Context"],
            cognitoIdentity = req.headers["Lambda-Runtime-Cognito-Identity"],
            functionArn = req.headers["Lambda-Runtime-Invoked-Function-Arn"]
                ?: throw BadRequestException("Missing Lambda-Runtime-Invoked-Function-Arn", requestId),
            deadline = req.headers["Lambda-Runtime-Deadline-Ms"]?.toLong()
                ?: throw BadRequestException("Lambda-Runtime-Deadline-Ms", requestId)
        )
    }
}