package runtime.client

import kotlinx.cinterop.toKString
import platform.posix.getenv

object EnvironmentConfiguration {
    val handler = getenv("_HANDLER")?.toKString()
    val lambdaTaskRoot = getenv("AWS_LAMBDA_TASK_ROOT")?.toKString()
    val lambdaRuntimeApi = getenv("AWS_LAMBDA_RUNTIME_API")?.toKString()
}