package com.batchofcode.lambdaruntime.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.curl.Curl

actual object KtorClient {
    actual val client = HttpClient(Curl.create())
}