package com.batchofcode.lambdaruntime.http

import io.ktor.client.HttpClient

expect class KtorClient {
    fun getClient(): HttpClient
}