package com.statsradio.lambdas

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.statsradio.lambdas.contexts.LambdaContext

abstract class LambdaHandler<I, O>(
    private val lambdaContext: LambdaContext
) : RequestHandler<I, O> {

    override fun handleRequest(input: I, awsRuntimeContext: Context): O {
        val logger = lambdaContext.logger

        try {
            logger.recordRequest(input!!, awsRuntimeContext)
            val response = handle(input, awsRuntimeContext)
            logger.recordResponse(response!!, awsRuntimeContext)

            return response
        } catch (expected: Exception) {
            logger.recordError(expected, awsRuntimeContext)
            throw expected
        }
    }

    protected abstract fun handle(input: I, context: Context): O
}
