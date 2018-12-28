package runtime.client

import platform.posix.*
import kotlinx.cinterop.*

object EnvironmentConfiguration {
    val handler = getenv("_HANDLER")?.toKString()
    val lambdaTaskRoot = getenv("AWS_LAMBDA_TASK_ROOT")?.toKString()
    val lambdaRuntimeApi = getenv("AWS_LAMBDA_RUNTIME_API")?.toKString()
}