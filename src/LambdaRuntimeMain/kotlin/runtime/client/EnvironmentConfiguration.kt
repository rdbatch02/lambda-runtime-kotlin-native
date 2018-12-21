package runtime

import platform.posix.*
import kotlinx.cinterop.*

object EnvironmentConfiguration {
    val lambdaRuntimeApi = getenv("AWS_LAMBDA_RUNTIME_API")?.toKString()

}