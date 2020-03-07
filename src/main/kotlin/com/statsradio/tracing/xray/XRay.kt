package com.statsradio.tracing.xray

import com.amazonaws.xray.exceptions.AlreadyEmittedException
import com.amazonaws.xray.exceptions.SegmentNotFoundException
import com.amazonaws.xray.exceptions.SubsegmentNotFoundException
import org.apache.logging.log4j.kotlin.Logging
import java.io.IOException

object XRay : Logging {

    val exceptions = setOf(
        AlreadyEmittedException::class,
        SegmentNotFoundException::class,
        SubsegmentNotFoundException::class,
        IllegalStateException::class,
        IOException::class
    )

    @Throws(Exception::class)
    fun suppressXRayException(exception: Exception) {
        val shouldNotThrow = exceptions.any { it.isInstance(exception) }

        if (shouldNotThrow) {
            logger.warn(exception)
            return
        }

        throw exception
    }
}
