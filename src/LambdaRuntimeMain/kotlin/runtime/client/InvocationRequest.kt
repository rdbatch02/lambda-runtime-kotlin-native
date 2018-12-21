data class InvocationRequest (
    val payload: String,
    val requestId: String,
    val xrayTraceId: String,
    val clientContext: String,
    val cognitoIdentity: String,
    val functionArn: String,
    val deadline: Long
)