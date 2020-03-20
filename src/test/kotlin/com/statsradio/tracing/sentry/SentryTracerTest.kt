package com.statsradio.tracing.sentry

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.every
import io.mockk.mockk
import io.sentry.SentryClient
import io.sentry.context.Context
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

internal class SentryTracerTest {

    companion object {
        private val NOW = Instant.now()
        private val CLOCK = Clock.fixed(NOW, ZoneOffset.UTC)
    }

    private val context = mockk<Context>()
    private val client = mockk<SentryClient> {
        val testScope = this@SentryTracerTest
        every { context } returns testScope.context
    }

    @Test
    fun `should create sentry trace`() {
        val tracer = SentryTracer(CLOCK, client)

        val trace = tracer.openTrace("Bob")

        val expected = SentryTrace(context = context, message = "Bob", startOfTrace = NOW)
        assertThat(trace).isEqualTo(expected)
    }
}
