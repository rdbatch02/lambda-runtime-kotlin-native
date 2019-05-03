package com.batchofcode.lambdaruntime.client

expect object EnvironmentConfiguration {
    val handler: String?
    val lambdaTaskRoot: String?
    val lambdaRuntimeApi: String?
}