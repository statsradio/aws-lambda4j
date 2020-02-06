package com.statsradio.logging

import org.apache.logging.log4j.Level

interface EventLogger {

    fun recordEvent(type: String, message: String, metadata: Map<String, String> = mapOf(), level: Level = Level.DEBUG)
}
