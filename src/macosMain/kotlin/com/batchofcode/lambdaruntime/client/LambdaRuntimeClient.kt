package com.batchofcode.lambdaruntime.client

import com.batchofcode.lambdaruntime.handler.InvocationRequest
import com.batchofcode.lambdaruntime.http.KtorClient
import kotlinx.coroutines.runBlocking

actual class LambdaRuntimeClient {
    actual fun run(handler: (InvocationRequest) -> String) {
        println("WARNING! Runtime not supported on MacOS Platform.")
        runBlocking { CommonAwsRuntimeClient(KtorClient().getClient()).run(handler) }
    }
}