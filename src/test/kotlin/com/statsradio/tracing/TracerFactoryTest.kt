package com.statsradio.tracing

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isInstanceOf
import com.statsradio.tracing.log4j.Log4JTracer
import com.statsradio.tracing.sentry.SentryTracer
import com.statsradio.tracing.xray.XRayTracer
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

internal class TracerFactoryTest {

    companion object {

        private val NOW = Instant.now()
        private val CLOCK = Clock.fixed(NOW, ZoneOffset.UTC)
    }

    private val tracerFactory = TracerFactory(CLOCK)

    @Test
    fun `given no xray nor sentry enabled, should only have log4j tracer`() {
        val settings = Settings(enableSentry = false, enableXRay = false)

        val tracer = tracerFactory.create(settings) as MultipleTracers

        val tracers = tracer.tracers
        assertThat(tracers).hasSize(1)
        assertThat(tracers[0]).isInstanceOf(Log4JTracer::class)
    }

    @Test
    fun `given xray enabled, should have xray tracer`() {
        val settings = Settings(enableSentry = false, enableXRay = true)

        val tracer = tracerFactory.create(settings) as MultipleTracers

        val tracers = tracer.tracers
        assertThat(tracers).hasSize(1)
        assertThat(tracers[0]).isInstanceOf(XRayTracer::class)
    }

    @Test
    fun `given sentry enabled, should have sentry tracer`() {
        val settings = Settings(enableSentry = true, enableXRay = false)

        val tracer = tracerFactory.create(settings) as MultipleTracers

        val tracers = tracer.tracers
        assertThat(tracers).hasSize(1)
        assertThat(tracers[0]).isInstanceOf(SentryTracer::class)
    }

    @Test
    fun `given xray and sentry enabled, should have both tracers`() {
        val settings = Settings(enableSentry = true, enableXRay = true)

        val tracer = tracerFactory.create(settings) as MultipleTracers

        val tracers = tracer.tracers
        assertThat(tracers).hasSize(2)
        assertThat(tracers[0]).isInstanceOf(XRayTracer::class)
        assertThat(tracers[1]).isInstanceOf(SentryTracer::class)
    }
}
