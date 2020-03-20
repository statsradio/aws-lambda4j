package com.statsradio.tracing.sentry

import com.statsradio.tracing.Trace
import io.sentry.context.Context
import io.sentry.event.Breadcrumb
import io.sentry.event.BreadcrumbBuilder
import java.time.Instant
import java.util.*

data class SentryTrace(
    private val context: Context,
    private val message: String,
    private val startOfTrace: Instant
) : Trace {

    private var level = Breadcrumb.Level.INFO

    private val tags = mutableMapOf<String, String>()
    private val metadata = mutableMapOf<String, String>()

    /**
     * Changes the breadcrumb Level to ERROR
     * Puts a field "error" with exception in breadcrumb data
     */
    override fun signalError(error: Exception) {
        this.level = Breadcrumb.Level.ERROR
        setTag("error", error.toString())
    }

    /**
     * Puts the field in breadcrumb data
     */
    override fun setTag(name: String, value: String) {
        tags[name] = value
    }

    /**
     * Puts the field in breadcrumb data
     */
    override fun setMetadata(key: String, value: String) {
        metadata[key] = value
    }

    /**
     * Creates and records the breadcrumb
     */
    override fun close() {
        val breadcrumb = BreadcrumbBuilder()
            .setMessage(message)
            .setLevel(level)
            .setTimestamp(Date.from(startOfTrace))
            .setCategory("trace")
            .setData(tags + metadata)
            .build()

        context.recordBreadcrumb(breadcrumb)
    }
}
