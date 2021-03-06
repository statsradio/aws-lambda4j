package com.statsradio.lambdas.logging

import com.amazonaws.services.lambda.runtime.Context
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.jupiter.api.Test

class LambdaMultipleLoggerTest {

    companion object {
        const val REQUEST = "request"
        const val RESPONSE = "response"
        const val EVENT_TYPE = "event"
        const val EVENT = "this is an event message"

        val ERROR = Exception("error")
    }

    private val context = mockk<Context>()

    private val logger1 = mockk<LambdaLogger>(relaxed = true)
    private val logger2 = mockk<LambdaLogger>(relaxed = true)
    private val multipleLogger = LambdaMultipleLogger(listOf(logger1, logger2))

    @Test
    fun `should record request in all loggers`() {
        multipleLogger.recordRequest(REQUEST, context)

        verifyOrder {
            logger1.recordRequest(REQUEST, context)
            logger2.recordRequest(REQUEST, context)
        }
    }

    @Test
    fun `should record response in all loggers`() {
        multipleLogger.recordResponse(RESPONSE, context)

        verifyOrder {
            logger1.recordResponse(RESPONSE, context)
            logger2.recordResponse(RESPONSE, context)
        }
    }

    @Test
    fun `should record handler error in all loggers`() {
        multipleLogger.recordHandlerError(ERROR, context)

        verifyOrder {
            logger1.recordHandlerError(ERROR, context)
            logger2.recordHandlerError(ERROR, context)
        }
    }

    @Test
    fun `should record events in all loggers`() {
        multipleLogger.recordEvent(EVENT_TYPE, EVENT)

        verifyOrder {
            logger1.recordEvent(EVENT_TYPE, EVENT)
            logger2.recordEvent(EVENT_TYPE, EVENT)
        }
    }

    @Test
    fun `should record error in all loggers`() {
        multipleLogger.recordError(EVENT_TYPE, ERROR)

        verifyOrder {
            logger1.recordError(EVENT_TYPE, ERROR)
            logger2.recordError(EVENT_TYPE, ERROR)
        }
    }
}
