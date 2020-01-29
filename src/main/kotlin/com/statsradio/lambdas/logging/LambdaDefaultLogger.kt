package com.statsradio.lambdas.logging

import com.amazonaws.services.lambda.runtime.Context
import com.google.gson.Gson
import org.apache.logging.log4j.kotlin.Logging

/**
 * Log4J2 lambda lifecycle logger
 *
 * Records event to display in your preferred integration
 */
class LambdaDefaultLogger(private val gson: Gson = Gson()) : LambdaLogger {

    companion object : Logging

    override fun recordRequest(request: Any, awsRuntimeContext: Context) {
        logger.debug("Request : $request")
        logRuntimeContext(awsRuntimeContext)
    }

    override fun recordResponse(response: Any, awsRuntimeContext: Context) {
        logger.debug("Response : $response")
    }

    override fun recordError(error: Exception, awsRuntimeContext: Context) {
        logger.error("Error", error)
    }

    override fun recordEvent(type: String, message: String, metadata: Map<String, String>) {
        logger.info("$type => $message: \n$metadata")
    }

    private fun logRuntimeContext(awsRuntimeContext: Context) {
        logger.debug("Context : ${gson.toJson(awsRuntimeContext)}")
    }
}
