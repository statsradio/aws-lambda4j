package com.statsradio.lambdas.tracing

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.statsradio.lambdas.contexts.env.TestEnvLoader
import com.statsradio.tracing.Settings
import com.statsradio.tracing.Tracer
import com.statsradio.tracing.TracerFactory
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import org.apache.http.impl.client.HttpClientBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class LambdaTracingFactoryTest {

    private val tracerFactory = mockk<TracerFactory>()
    private val expectedTracer = mockk<Tracer>()

    @BeforeEach
    internal fun setUp() {
        clearMocks(expectedTracer, tracerFactory)
    }

    @Test
    fun `given no xray configuration, should create tracer only with sentry`() {
        val envLoader = TestEnvLoader(emptyMap())
        every { tracerFactory.create(Settings(enableXRay = false, enableSentry = true)) } returns expectedTracer

        val tracer = LambdaTracingFactory(envLoader, tracerFactory).createTracer()

        assertThat(tracer).isEqualTo(expectedTracer)
    }

    @Test
    fun `given xray disabled, should create tracer only with sentry`() {
        val envLoader = TestEnvLoader(mapOf("XRAY_TRACING_ENABLED" to "false"))
        every { tracerFactory.create(Settings(enableXRay = false, enableSentry = true)) } returns expectedTracer

        val tracer = LambdaTracingFactory(envLoader, tracerFactory).createTracer()

        assertThat(tracer).isEqualTo(expectedTracer)
    }

    @Test
    fun `given xray enabled, should create tracer only both xray and sentry`() {
        val envLoader = TestEnvLoader(mapOf("XRAY_TRACING_ENABLED" to "true"))
        every { tracerFactory.create(Settings(enableXRay = true, enableSentry = true)) } returns expectedTracer

        val tracer = LambdaTracingFactory(envLoader, tracerFactory).createTracer()

        assertThat(tracer).isEqualTo(expectedTracer)
    }

    @Test
    fun `given no xray configuration, should create default apache http client builder`() {
        val envLoader = TestEnvLoader(emptyMap())

        val httpClientBuilder = LambdaTracingFactory(envLoader, tracerFactory).createHttpClientBuilder()

        assertThat(httpClientBuilder).isInstanceOf(HttpClientBuilder::class)
    }

    @Test
    fun `given xray disabled, should create default apache http client builder`() {
        val envLoader = TestEnvLoader(mapOf("XRAY_TRACING_ENABLED" to "false"))

        val httpClientBuilder = LambdaTracingFactory(envLoader, tracerFactory).createHttpClientBuilder()

        assertThat(httpClientBuilder).isInstanceOf(HttpClientBuilder::class)
    }

    @Test
    fun `given xray enabled, should create xray proxied apache http client builder`() {
        val envLoader = TestEnvLoader(mapOf("XRAY_TRACING_ENABLED" to "true"))

        val httpClientBuilder = LambdaTracingFactory(envLoader, tracerFactory).createHttpClientBuilder()

        assertThat(httpClientBuilder).isInstanceOf(com.amazonaws.xray.proxies.apache.http.HttpClientBuilder::class)
    }
}
