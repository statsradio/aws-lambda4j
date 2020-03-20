package com.statsradio.tracing

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class MultipleTracersTest {

    companion object {
        private const val NAME = "Name"
    }

    private val trace1 = mockk<Trace>(relaxed = true)
    private val trace2 = mockk<Trace>(relaxed = true)
    private val tracer1 = mockk<Tracer>(relaxed = true) { every { openTrace(NAME) } returns trace1 }
    private val tracer2 = mockk<Tracer>(relaxed = true) { every { openTrace(NAME) } returns trace2 }

    private val tracers = MultipleTracers(listOf(tracer1, tracer2))

    @Test
    fun `when close, should close all traces`() {
        tracers.trace(NAME) { }

        verify {
            trace1.close()
            trace2.close()
        }
    }
}
