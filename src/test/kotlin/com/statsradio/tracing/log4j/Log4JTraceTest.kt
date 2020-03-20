package com.statsradio.tracing.log4j

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import org.apache.logging.log4j.CloseableThreadContext
import org.apache.logging.log4j.ThreadContext
import org.apache.logging.log4j.kotlin.KotlinLogger
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class Log4JTraceTest {

    companion object {
        const val ID = "12345"
        const val NAME = "Name"
    }

    private val logger = mockk<KotlinLogger>(relaxed = true)

    @BeforeEach
    internal fun setUp() {
        clearMocks(logger)
    }

    @Test
    fun `when create, should log info`() {
        val context = CloseableThreadContext.push("Bob")

        withContext(context).use {
            verify { logger.info("Open Trace: $ID - $NAME") }
        }
    }

    @Test
    fun `should push tag to MDC context`() {
        val context = CloseableThreadContext.push("Bob")

        withContext(context).use {
            it.setTag("Bob", "NotBob")

            val mdc = ThreadContext.getContext()
            assertThat(mdc).contains("Bob", "NotBob")
        }
    }

    @Test
    fun `should push metadata to MDC context`() {
        val context = CloseableThreadContext.push("Bob")

        withContext(context).use {
            it.setMetadata("Bob", "NotBob")

            val mdc = ThreadContext.getContext()
            assertThat(mdc).contains("Bob", "NotBob")
        }
    }

    @Test
    fun `given signal error, when close, should log error`() {
        val error = Exception()
        val context = CloseableThreadContext.push("Bob")

        withContext(context).use {
            it.signalError(error)
        }

        verify { logger.error("Close Trace: $ID - $NAME", error) }
    }

    @Test
    fun `given no signal error, when close, should log info`() {
        val context = CloseableThreadContext.push("Bob")

        withContext(context).use { }

        verify { logger.info("Close Trace: $ID - $NAME") }
    }

    @Test
    fun `when close, should clean MDC context`() {
        val context = CloseableThreadContext.push("Bob")

        withContext(context).use {
            it.setTag("Bob", "NotBob")
            it.setMetadata("BobMeta", "NotBobMeta")
        }

        val mdc = ThreadContext.getContext()
        assertThat(mdc).doesNotContain("Bob", "NotBob")
        assertThat(mdc).doesNotContain("BobMeta", "NotBobMeta")
    }

    private fun withContext(context: CloseableThreadContext.Instance): Log4JTrace {
        return Log4JTrace(context, logger, ID, NAME)
    }
}
