package com.statsradio.tracing.log4j

import com.statsradio.tracing.Trace
import com.statsradio.tracing.Tracer
import org.apache.logging.log4j.CloseableThreadContext
import org.apache.logging.log4j.kotlin.Logging
import java.util.*

class Log4JTracer : Tracer {

    companion object : Logging

    override fun <T> trace(name: String, toTrace: (trace: Trace) -> T): T {
        val traceId = UUID.randomUUID().toString()
        val context = CloseableThreadContext.put("traceId", traceId)

        Log4JTrace(context).use { trace ->
            logger.trace(name)
            return toTrace(trace)
        }
    }
}
