package runtime.client

import httprekwest.HttpResponse
import runtime.client.exception.BadRequestException
import runtime.client.exception.MissingRequestIdException
import runtime.handler.InvocationRequest

object RequestMapper {
    fun mapRequest(req: HttpResponse): InvocationRequest {
        val requestId = req.headers["Lambda-Runtime-Aws-Request-Id"] ?: throw MissingRequestIdException("Missing Lambda-Runtime-Aws-Request-Id")
        return InvocationRequest(
                payload = req.body,
                requestId = requestId,
                xrayTraceId = req.headers["Lambda-Runtime-Trace-Id"],
                clientContext = req.headers["Lambda-Runtime-Client-Context"],
                cognitoIdentity = req.headers["Lambda-Runtime-Cognito-Identity"],
                functionArn = req.headers["Lambda-Runtime-Invoked-Function-Arn"] ?: throw BadRequestException("Missing Lambda-Runtime-Invoked-Function-Arn", requestId),
                deadline = req.headers["Lambda-Runtime-Deadline-Ms"]?.toLong() ?: throw BadRequestException("Lambda-Runtime-Deadline-Ms", requestId)
        )
    }
}