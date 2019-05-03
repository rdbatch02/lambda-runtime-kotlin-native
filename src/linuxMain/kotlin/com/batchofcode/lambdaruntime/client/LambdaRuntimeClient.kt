package com.batchofcode.lambdaruntime.client

import kotlinx.coroutines.runBlocking
import com.batchofcode.lambdaruntime.handler.InvocationRequest
import com.batchofcode.lambdaruntime.http.KtorClient

actual class LambdaRuntimeClient {
    actual fun run(handler: (InvocationRequest) -> String) {
        runBlocking { CommonAwsRuntimeClient(KtorClient.client).run(handler) }
    }
}