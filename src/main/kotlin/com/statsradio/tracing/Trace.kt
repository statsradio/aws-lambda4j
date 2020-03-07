package com.statsradio.tracing

interface Trace : AutoCloseable {

    fun setTag(name: String, value: String)

    fun setMetadata(key: String, value: Any)
}
