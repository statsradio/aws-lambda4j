package com.statsradio.tracing.xray

import com.amazonaws.xray.AWSXRayRecorder
import com.amazonaws.xray.entities.Subsegment
import com.statsradio.tracing.Trace
import com.statsradio.tracing.Tracer

class XRayTracer(
    private val recorder: AWSXRayRecorder
) : Tracer {

    override fun <T> trace(name: String, toTrace: (trace: Trace) -> T): T {
        val subsegment = createSubsegment(name)
        XRayTrace(subsegment).use { trace ->
            return toTrace(trace)
        }
    }

    private fun createSubsegment(name: String): Subsegment? {
        return try {
            recorder.beginSubsegment(name)
        } catch (expected: Exception) {
            XRay.suppressXRayException(expected)
            null
        }
    }
}
