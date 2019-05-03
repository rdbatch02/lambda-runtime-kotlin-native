package com.batchofcode.lambdaruntime.http

import io.ktor.client.HttpClient

expect object KtorClient {
    val client: HttpClient
}