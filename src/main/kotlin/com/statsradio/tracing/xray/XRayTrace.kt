package com.statsradio.tracing.xray

import com.amazonaws.xray.entities.Entity
import com.statsradio.tracing.Trace
import org.apache.logging.log4j.kotlin.Logging

data class XRayTrace(
    val xrayEntity: Entity?
) : Trace {

    companion object : Logging

    override fun setTag(name: String, value: String) {
        try {
            xrayEntity?.putAnnotation(name, value)
        } catch (expected: Exception) {
            XRay.suppressXRayException(expected)
        }
    }

    override fun setMetadata(key: String, value: Any) {
        try {
            xrayEntity?.putMetadata(key, value)
        } catch (expected: Exception) {
            XRay.suppressXRayException(expected)
        }
    }

    override fun close() {
        try {
            xrayEntity?.close()
        } catch (expected: Exception) {
            XRay.suppressXRayException(expected)
        }
    }
}
