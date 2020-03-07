package com.statsradio.tracing.xray

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.amazonaws.xray.AWSXRayRecorder
import com.amazonaws.xray.entities.Subsegment
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.reflect.full.createInstance

internal class XRayTracerTest {

    companion object {
        private const val TRACE_NAME = "Bob"

        @JvmStatic
        fun xrayExceptions() = XRay.exceptions.map { Arguments.of(it.createInstance()) }
    }

    private val subsegment = mockk<Subsegment>(relaxed = true)
    private val recorder = mockk<AWSXRayRecorder>()

    @BeforeEach
    internal fun setUp() {
        clearMocks(recorder, subsegment)

        every { recorder.beginSubsegment(TRACE_NAME) } returns subsegment
    }

    @Test
    fun `should create trace with subsegment`() {
        val tracer = XRayTracer(recorder)

        tracer.trace(TRACE_NAME) { trace ->
            val expected = XRayTrace(subsegment)
            assertThat(trace).isEqualTo(expected)
        }
    }

    @ParameterizedTest
    @MethodSource("xrayExceptions")
    fun <E : Exception> `given xray exception, should create trace with no subsegment`(exception: E) {
        every { recorder.beginSubsegment(TRACE_NAME) } throws exception

        val tracer = XRayTracer(recorder)

        tracer.trace(TRACE_NAME) { trace ->
            val expected = XRayTrace(null)
            assertThat(trace).isEqualTo(expected)
        }
    }

    @Test
    fun `given non xray exception, should throw exception`() {
        every { recorder.beginSubsegment(TRACE_NAME) } throws InterruptedException()

        val tracer = XRayTracer(recorder)

        assertThrows<InterruptedException> { tracer.trace(TRACE_NAME) { } }
    }

    @Test
    fun `should close subsegment`() {
        val tracer = XRayTracer(recorder)

        val value = tracer.trace(TRACE_NAME) { 1 }

        assertThat(value).isEqualTo(1)
        verify { subsegment.close() }
    }

    @Test
    fun `given error on execution, should close subsegment`() {
        val tracer = XRayTracer(recorder)

        assertThrows<IllegalArgumentException> { tracer.trace(TRACE_NAME) { throw IllegalArgumentException("Wassup") } }
        verify { subsegment.close() }
    }
}
