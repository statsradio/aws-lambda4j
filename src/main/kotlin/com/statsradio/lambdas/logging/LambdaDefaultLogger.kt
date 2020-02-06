package com.statsradio.lambdas.logging

import com.amazonaws.services.lambda.runtime.Context
import com.google.gson.Gson
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.kotlin.Logging

/**
 * Log4J2 lambda lifecycle logger
 *
 * Records event to display in your preferred integration
 */
class LambdaDefaultLogger(
    private val level: Level = Level.DEBUG,
    private val gson: Gson = Gson()
) : LambdaLogger {

    companion object : Logging

    override fun recordRequest(request: Any, awsRuntimeContext: Context) {
        logger.log(level, "Request : $request")
        logRuntimeContext(awsRuntimeContext)
    }

    override fun recordResponse(response: Any, awsRuntimeContext: Context) {
        logger.log(level, "Response : $response")
    }

    override fun recordError(error: Exception, awsRuntimeContext: Context) {
        logger.error("Error", error)
    }

    override fun recordEvent(type: String, message: String, metadata: Map<String, String>, level: Level) {
        logger.log(level, "$type => $message: \n$metadata")
    }

    private fun logRuntimeContext(awsRuntimeContext: Context) {
        logger.log(level, "Context : ${gson.toJson(awsRuntimeContext)}")
    }
}
