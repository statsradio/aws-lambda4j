package com.statsradio.lambdas.tracing

import com.statsradio.lambdas.contexts.env.LambdaEnvLoader
import com.statsradio.tracing.Settings
import com.statsradio.tracing.Tracer
import com.statsradio.tracing.TracerFactory
import org.apache.http.impl.client.HttpClientBuilder

class LambdaTracingFactory(
    private val envLoader: LambdaEnvLoader,
    private val tracerFactory: TracerFactory
) {

    fun createTracer(): Tracer {
        val tracerSettings = Settings(
            enableXRay = isXRayTracingEnabled(),
            enableSentry = true
        )

        return tracerFactory.create(tracerSettings)
    }

    fun createHttpClientBuilder(): HttpClientBuilder {
        if (isXRayTracingEnabled()) {
            return com.amazonaws.xray.proxies.apache.http.HttpClientBuilder.create()
        }

        return HttpClientBuilder.create()
    }

    private fun isXRayTracingEnabled(): Boolean {
        val enabled = envLoader.tryLoad("XRAY_TRACING_ENABLED") ?: "false"
        return enabled.toBoolean()
    }
}
