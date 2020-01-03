package com.statsradio.lambdas.logging

import com.amazonaws.services.lambda.runtime.Context
import com.statsradio.logging.EventLogger

/**
 * LambdaLogger interface to log lifecycle information
 */
interface LambdaLogger : EventLogger {

    /**
     * @param request serializable lambda request
     */
    fun recordRequest(request: Any, awsRuntimeContext: Context)

    /**
     * @param response serializable lambda response
     */
    fun recordResponse(response: Any, awsRuntimeContext: Context)

    /**
     * @param error caught error during lambda execution
     */
    fun recordError(error: Exception, awsRuntimeContext: Context)
}
