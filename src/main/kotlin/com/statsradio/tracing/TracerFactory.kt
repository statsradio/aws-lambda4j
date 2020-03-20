package com.statsradio.tracing

import com.amazonaws.xray.AWSXRay
import com.statsradio.tracing.log4j.Log4JTracer
import com.statsradio.tracing.sentry.SentryTracer
import com.statsradio.tracing.xray.XRayTracer
import io.sentry.Sentry
import java.time.Clock

data class Settings(
    val enableXRay: Boolean,
    val enableSentry: Boolean
)

class TracerFactory(private val clock: Clock) {

    fun create(settings: Settings): Tracer {
        val tracers = mutableListOf<Tracer>()

        if (settings.enableXRay) {
            tracers += createXRayTracer()
        }

        if (settings.enableSentry) {
            tracers += createSentryTracer()
        }

        if (tracers.isEmpty()) {
            tracers += Log4JTracer()
        }

        return MultipleTracers(tracers)
    }

    private fun createXRayTracer(): XRayTracer {
        val recorder = AWSXRay.getGlobalRecorder()
        return XRayTracer(recorder)
    }

    private fun createSentryTracer(): SentryTracer {
        val client = Sentry.getStoredClient()
        return SentryTracer(clock, client)
    }
}
