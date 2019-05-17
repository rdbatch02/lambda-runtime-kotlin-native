package com.batchofcode.lambdaruntime.client

import com.batchofcode.lambdaruntime.handler.InvocationRequest
import com.batchofcode.lambdaruntime.http.KtorClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

actual class LambdaRuntimeClient {
    actual fun run(handler: (InvocationRequest) -> String) {
        GlobalScope.launch(Dispatchers.Unconfined) { CommonAwsRuntimeClient(KtorClient().getClient()).run(handler) }
    }
}