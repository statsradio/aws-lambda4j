package com.statsradio.tracing

interface Tracer {

    /**
     * Opens a manual trace
     * If you want automatic error-handling and closing, trace("").use { trace -> ... } is suggested
     */
    fun openTrace(name: String): Trace

    /**
     * Utility method to execute a command with a trace
     *
     * @param name name of the trace
     * @param toTrace callback to execute with the trace
     * @exception exception thrown by callback
     * @return callback result
     */
    fun <T> trace(name: String, toTrace: (trace: Trace) -> T): T {
        return openTrace(name).use {
            try {
                toTrace(it)
            } catch (expected: Exception) {
                it.signalError(expected)
                throw expected
            }
        }
    }
}
