package com.batchofcode.lambdaruntime.util

import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder

fun HeadersBuilder.fromMap(headers: Map<String, String>) {
    this.clear()
    headers.forEach {
        this.append(it.key, it.value)
    }
}

fun Headers.containsKey(key: String): Boolean {
    return this.entries().any { it.key == key }
}