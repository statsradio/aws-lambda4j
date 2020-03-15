package com.statsradio.tracing.log4j

import com.statsradio.tracing.Trace
import org.apache.logging.log4j.CloseableThreadContext

class Log4JTrace(
    private val context: CloseableThreadContext.Instance
) : Trace {

    override fun setTag(name: String, value: String) {
        context.put(name, value)
    }

    override fun setMetadata(key: String, value: Any) {
        context.put(key, value.toString())
    }

    override fun close() {
        context.close()
    }
}
