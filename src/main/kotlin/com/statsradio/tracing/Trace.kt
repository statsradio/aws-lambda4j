package com.statsradio.tracing

interface Trace : AutoCloseable {

    /**
     * Signals an exception has happened during the trace
     */
    fun signalError(error: Exception)

    /**
     * Overrides tag in the trace
     */
    fun setTag(name: String, value: String)

    /**
     * Overrides metadata in the trace
     */
    fun setMetadata(key: String, value: String)
}
