package com.statsradio.lambdas

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.amazonaws.services.lambda.runtime.Context
import com.statsradio.lambdas.contexts.LambdaContext
import com.statsradio.lambdas.logging.LambdaLogger
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LambdaHandlerTest {

    companion object {
        const val INPUT = "hello"
        const val EXPECTED_OUTPUT = "hello hello"
    }

    private val lambdaRuntimeContext = mockk<Context>()

    private val lambdaContext = mockk<LambdaContext>()
    private val lambdaLogger = mockk<LambdaLogger>(relaxed = true)

    @BeforeEach
    fun setUp() {
        clearMocks(lambdaContext, lambdaLogger)
        every { lambdaContext.logger } returns lambdaLogger
    }

    @Test
    fun `should execute lambda`() {
        val lambdaHandler = StubLambdaHandler(lambdaContext)

        val output = lambdaHandler.handleRequest(INPUT, lambdaRuntimeContext)

        assertThat(output).isEqualTo(EXPECTED_OUTPUT)
    }

    @Test
    fun `should trace request then response`() {
        val lambdaHandler = StubLambdaHandler(lambdaContext)

        lambdaHandler.handleRequest(INPUT, lambdaRuntimeContext)

        verifyOrder {
            lambdaLogger.recordRequest(INPUT, lambdaRuntimeContext)
            lambdaLogger.recordResponse(EXPECTED_OUTPUT, lambdaRuntimeContext)
        }
    }

    @Test
    fun `given an execution error, should throw`() {
        val error = Exception("runtime lambda error")
        val lambdaHandler = WithExecutionErrorLambdaHandler(lambdaContext, error)

        val thrown = assertThrows<Exception> { lambdaHandler.handleRequest(INPUT, lambdaRuntimeContext) }

        assertThat(thrown).isEqualTo(error)
    }

    @Test
    fun `given an execution error, should trace request then error`() {
        val error = Exception("runtime lambda error")
        val lambdaHandler = WithExecutionErrorLambdaHandler(lambdaContext, error)

        assertThrows<Exception> { lambdaHandler.handleRequest(INPUT, lambdaRuntimeContext) }
        verifyOrder {
            lambdaLogger.recordRequest(INPUT, lambdaRuntimeContext)
            lambdaLogger.recordError(error, lambdaRuntimeContext)
        }
    }
}

class StubLambdaHandler(context: LambdaContext) : LambdaHandler<String, String>(context) {
    override fun handle(input: String, context: Context): String {
        return "$input $input"
    }
}

class WithExecutionErrorLambdaHandler(
    context: LambdaContext,
    private val error: Exception
) : LambdaHandler<String, String>(context) {
    override fun handle(input: String, context: Context): String {
        throw error
    }
}
