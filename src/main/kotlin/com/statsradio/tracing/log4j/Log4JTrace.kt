package com.statsradio.tracing.log4j

import com.statsradio.tracing.Trace
import org.apache.logging.log4j.CloseableThreadContext
import org.apache.logging.log4j.kotlin.KotlinLogger

class Log4JTrace(
    private val context: CloseableThreadContext.Instance,
    private val logger: KotlinLogger,
    private val id: String,
    private val name: String
) : Trace {

    init {
        logger.info("Open Trace: $id - $name")
    }

    private var error: Exception? = null

    override fun signalError(error: Exception) {
        this.error = error
    }

    override fun setTag(name: String, value: String) {
        context.put(name, value)
    }

    override fun setMetadata(key: String, value: String) {
        context.put(key, value)
    }

    override fun close() {
        val message = "Close Trace: $id - $name"
        when (error) {
            null -> logger.info(message)
            else -> logger.error(message, error)
        }

        context.close()
    }
}
