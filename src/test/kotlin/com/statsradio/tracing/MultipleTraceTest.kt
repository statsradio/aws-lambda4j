package com.statsradio.tracing

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class MultipleTraceTest {

    private val trace1 = mockk<Trace>(relaxed = true)
    private val trace2 = mockk<Trace>(relaxed = true)

    private val multipleTrace = MultipleTrace(listOf(trace1, trace2))

    @Test
    fun `should signal error to all trace`() {
        val error = Exception("Error")

        multipleTrace.signalError(error)

        verify {
            trace1.signalError(error)
            trace2.signalError(error)
        }
    }

    @Test
    fun `should set tag in all trace`() {
        multipleTrace.setTag("tag", "value")

        verify {
            trace1.setTag("tag", "value")
            trace2.setTag("tag", "value")
        }
    }

    @Test
    fun `should set metadata in all trace`() {
        multipleTrace.setMetadata("tag", "value")

        verify {
            trace1.setMetadata("tag", "value")
            trace2.setMetadata("tag", "value")
        }
    }

    @Test
    fun `should close all trace`() {
        multipleTrace.close()

        verify {
            trace1.close()
            trace2.close()
        }
    }
}
