package com.batchofcode.lambdaruntime.client

import com.batchofcode.lambdaruntime.handler.InvocationRequest

expect class LambdaRuntimeClient() {
    fun run(handler: (InvocationRequest) -> String)
}