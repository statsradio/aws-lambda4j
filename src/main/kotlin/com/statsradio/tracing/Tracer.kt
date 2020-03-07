package com.statsradio.tracing

interface Tracer {

    fun <T> trace(name: String, toTrace: (trace: Trace) -> T): T
}
