package com.batchofcode.lambdaruntime

internal expect fun <T> runTest(block: suspend () -> T): T