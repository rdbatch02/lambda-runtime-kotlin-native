package com.batchofcode.lambdaruntime.client

import kotlinx.cinterop.toKString
import platform.posix.getenv

actual object EnvironmentConfiguration {
    actual val handler = getenv("_HANDLER")?.toKString()
    actual val lambdaTaskRoot = getenv("AWS_LAMBDA_TASK_ROOT")?.toKString()
    actual val lambdaRuntimeApi = getenv("AWS_LAMBDA_RUNTIME_API")?.toKString()
}