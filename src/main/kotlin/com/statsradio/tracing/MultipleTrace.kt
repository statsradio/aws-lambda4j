package com.statsradio.tracing

class MultipleTrace(
    private val traces: List<Trace>
) : Trace {

    override fun signalError(error: Exception) {
        traces.forEach { it.signalError(error) }
    }

    override fun setTag(name: String, value: String) {
        traces.forEach { it.setTag(name, value) }
    }

    override fun setMetadata(key: String, value: String) {
        traces.forEach { it.setMetadata(key, value) }
    }

    override fun close() {
        traces.forEach { it.close() }
    }
}
