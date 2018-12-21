package runtime.client.exception

class BadRequestException(message: String, val requestId: String): Exception(message)