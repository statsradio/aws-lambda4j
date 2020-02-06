package com.statsradio.lambdas.logging

import com.amazonaws.services.lambda.runtime.Context
import org.apache.logging.log4j.Level

/**
 * Combine multiple LambdaLogger together
 */
class LambdaMultipleLogger(
    private val loggers: List<LambdaLogger>
) : LambdaLogger {

    override fun recordResponse(response: Any, awsRuntimeContext: Context) {
        loggers.forEach { it.recordResponse(response, awsRuntimeContext) }
    }

    override fun recordHandlerError(error: Exception, awsRuntimeContext: Context) {
        loggers.forEach { it.recordHandlerError(error, awsRuntimeContext) }
    }

    override fun recordError(type: String, error: Exception, message: String?, metadata: Map<String, String>) {
        loggers.forEach { it.recordError(type, error, message, metadata) }
    }

    override fun recordRequest(request: Any, awsRuntimeContext: Context) {
        loggers.forEach { it.recordRequest(request, awsRuntimeContext) }
    }

    override fun recordEvent(type: String, message: String, metadata: Map<String, String>, level: Level) {
        loggers.forEach { it.recordEvent(type, message, metadata, level) }
    }
}
