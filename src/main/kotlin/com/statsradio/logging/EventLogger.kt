package com.statsradio.logging

import org.apache.logging.log4j.Level

interface EventLogger {

    fun recordEvent(type: String, message: String, metadata: Map<String, String> = mapOf(), level: Level = Level.DEBUG)

    fun recordError(
        type: String,
        error: Exception,
        message: String? = error.message,
        metadata: Map<String, String> = mapOf()
    )
}
