package com.statsradio.lambdas.io

/**
 * Utility extension method to validate required field in a serializable class
 */
fun <T> T?.fieldRequired(name: String): T {
    if (this == null) {
        throw LambdaEventParsingError("$name is required")
    }

    return this
}
