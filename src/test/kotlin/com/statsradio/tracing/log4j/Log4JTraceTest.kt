package com.statsradio.tracing.log4j

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import org.apache.logging.log4j.CloseableThreadContext
import org.apache.logging.log4j.ThreadContext
import org.junit.jupiter.api.Test

internal class Log4JTraceTest {

    @Test
    fun `should push tag to MDC context`() {
        val context = CloseableThreadContext.push("Bob")

        Log4JTrace(context).use {
            it.setTag("Bob", "NotBob")

            val mdc = ThreadContext.getContext()
            assertThat(mdc).contains("Bob", "NotBob")
        }
    }

    @Test
    fun `should push metadata to MDC context`() {
        val context = CloseableThreadContext.push("Bob")

        Log4JTrace(context).use {
            it.setMetadata("Bob", "NotBob")

            val mdc = ThreadContext.getContext()
            assertThat(mdc).contains("Bob", "NotBob")
        }
    }

    @Test
    fun `when close, should clean MDC context`() {
        val context = CloseableThreadContext.push("Bob")

        Log4JTrace(context).use {
            it.setTag("Bob", "NotBob")
            it.setMetadata("BobMeta", "NotBobMeta")
        }

        val mdc = ThreadContext.getContext()
        assertThat(mdc).doesNotContain("Bob", "NotBob")
        assertThat(mdc).doesNotContain("BobMeta", "NotBobMeta")
    }
}
