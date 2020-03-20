package com.statsradio.tracing.sentry

import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import io.sentry.context.Context
import io.sentry.event.Breadcrumb
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

internal class SentryTraceTest {

    companion object {
        private val NOW = Instant.now()
        private const val MESSAGE = "Bob"
    }

    private val sentryContext = mockk<Context>(relaxed = true)

    @BeforeEach
    internal fun setUp() {
        clearMocks(sentryContext)
    }

    @Test
    fun `when close, should build breadcrumb with data as tags and metadata`() {
        SentryTrace(sentryContext, MESSAGE, NOW).use {
            it.setTag("Tag1", "TagValue1")
            it.setMetadata("Meta1", "MetaValue1")
        }

        val expectedData = mapOf(
            "Tag1" to "TagValue1",
            "Meta1" to "MetaValue1"
        )
        verify {
            sentryContext.recordBreadcrumb(
                match { it.data == expectedData }
            )
        }
    }

    @Test
    fun `given error signaled, when close, should have error in data`() {
        val error = Exception("Error")

        SentryTrace(sentryContext, MESSAGE, NOW).use { it.signalError(error) }

        val expectedData = mapOf("error" to error.toString())
        verify {
            sentryContext.recordBreadcrumb(
                match { it.level == Breadcrumb.Level.ERROR && it.data == expectedData }
            )
        }
    }
}
