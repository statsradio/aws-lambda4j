package com.statsradio.lambdas.logging

import com.amazonaws.services.lambda.runtime.Context

/**
 * Combine multiple LambdaLogger together
 */
class LambdaMultipleLogger(
    private val loggers: List<LambdaLogger>
) : LambdaLogger {

    override fun recordResponse(response: Any, awsRuntimeContext: Context) {
        loggers.forEach { tracer -> tracer.recordResponse(response, awsRuntimeContext) }
    }

    override fun recordError(error: Exception, awsRuntimeContext: Context) {
        loggers.forEach { tracer -> tracer.recordError(error, awsRuntimeContext) }
    }

    override fun recordRequest(request: Any, awsRuntimeContext: Context) {
        loggers.forEach { tracer -> tracer.recordRequest(request, awsRuntimeContext) }
    }

    override fun recordEvent(type: String, message: String, metadata: Map<String, String>) {
        loggers.forEach { tracer -> tracer.recordEvent(type, message, metadata) }
    }
}
