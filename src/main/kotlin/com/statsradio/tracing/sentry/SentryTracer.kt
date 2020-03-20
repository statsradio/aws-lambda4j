package com.statsradio.tracing.sentry

import com.statsradio.tracing.Trace
import com.statsradio.tracing.Tracer
import io.sentry.SentryClient
import java.time.Clock

class SentryTracer(
    private val clock: Clock,
    private val client: SentryClient
) : Tracer {

    override fun openTrace(name: String): Trace {
        val now = clock.instant()
        return SentryTrace(client.context, message = name, startOfTrace = now)
    }
}
