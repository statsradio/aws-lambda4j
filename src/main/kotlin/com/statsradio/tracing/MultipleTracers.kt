package com.statsradio.tracing

class MultipleTracers(
    val tracers: List<Tracer>
) : Tracer {

    override fun openTrace(name: String): Trace {
        val traces = tracers.map { it.openTrace(name) }
        return MultipleTrace(traces)
    }
}
