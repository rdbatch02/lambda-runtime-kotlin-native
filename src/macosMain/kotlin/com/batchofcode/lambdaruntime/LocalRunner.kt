package com.batchofcode.lambdaruntime

import com.batchofcode.lambdaruntime.client.LambdaRuntimeClient

fun main() {
    LambdaRuntimeClient().run {
        println(it)
        "RESPONSE"
    }
}