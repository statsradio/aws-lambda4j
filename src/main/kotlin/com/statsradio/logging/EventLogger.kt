package com.statsradio.logging

interface EventLogger {

    fun recordEvent(type: String, message: String, metadata: Map<String, String> = mapOf())
}
