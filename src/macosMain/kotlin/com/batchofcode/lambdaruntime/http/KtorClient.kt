package com.batchofcode.lambdaruntime.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.curl.Curl

actual class KtorClient {
    private val client = HttpClient(Curl.create())
    actual fun getClient() = client
}