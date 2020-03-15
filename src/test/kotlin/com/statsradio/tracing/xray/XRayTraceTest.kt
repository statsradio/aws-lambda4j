package com.statsradio.tracing.xray

import assertk.assertAll
import com.amazonaws.xray.entities.Entity
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.reflect.full.createInstance

internal class XRayTraceTest {

    companion object {
        private const val TAG_NAME = "Bob"
        private const val TAG_VALUE = "NotBob"
        private const val METADATA_KEY = "BobMeta"
        private const val METADATA_VALUE = "BobNotMeta"

        @JvmStatic
        fun xrayExceptions() = XRay.exceptions.map { Arguments.of(it.createInstance()) }
    }

    private val entity = mockk<Entity>(relaxed = true)

    @BeforeEach
    internal fun setUp() {
        clearMocks(entity)
    }

    @Test
    fun `given entity, when set tag, should put in annotations`() {
        val trace = XRayTrace(entity)
        trace.setTag(TAG_NAME, TAG_VALUE)
        verify { entity.putAnnotation(TAG_NAME, TAG_VALUE) }
    }

    @Test
    fun `given no entity, when set tag, should do nothing in annotations`() {
        val trace = XRayTrace(null)
        trace.setTag(TAG_NAME, TAG_VALUE)
        verify(exactly = 0) { entity.putAnnotation(TAG_NAME, TAG_VALUE) }
    }

    @Test
    fun `given entity, when set metadata, should put in metadata`() {
        val trace = XRayTrace(entity)
        trace.setMetadata(METADATA_KEY, METADATA_VALUE)
        verify { entity.putMetadata(METADATA_KEY, METADATA_VALUE) }
    }

    @Test
    fun `given no entity, when set metadata, should do nothing in metadata`() {
        val trace = XRayTrace(null)
        trace.setMetadata(METADATA_KEY, METADATA_VALUE)
        verify(exactly = 0) { entity.putMetadata(METADATA_KEY, METADATA_VALUE) }
    }

    @Test
    fun `given entity, when close, should close it`() {
        val trace = XRayTrace(entity)
        trace.close()
        verify { entity.close() }
    }

    @Test
    fun `given no entity, when close, should do nothing`() {
        val trace = XRayTrace(null)
        trace.close()
        verify(exactly = 0) { entity.close() }
    }

    @ParameterizedTest
    @MethodSource("xrayExceptions")
    fun <E : Exception> `given xray exception, should not throw`(exception: E) {
        every { entity.putAnnotation(TAG_NAME, TAG_VALUE) } throws exception
        every { entity.putMetadata(METADATA_KEY, METADATA_VALUE) } throws exception
        every { entity.close() } throws exception

        val trace = XRayTrace(entity)

        assertAll {
            assertDoesNotThrow { trace.setTag(TAG_NAME, TAG_VALUE) }
            assertDoesNotThrow { trace.setMetadata(METADATA_KEY, METADATA_VALUE) }
            assertDoesNotThrow { trace.close() }
        }
    }

    @Test
    fun `given non xray exception, should throw it`() {
        val exception = InterruptedException()
        every { entity.putAnnotation(TAG_NAME, TAG_VALUE) } throws exception
        every { entity.putMetadata(METADATA_KEY, METADATA_VALUE) } throws exception
        every { entity.close() } throws exception

        val trace = XRayTrace(entity)

        assertAll {
            assertThrows<InterruptedException> { trace.setTag(TAG_NAME, TAG_VALUE) }
            assertThrows<InterruptedException> { trace.setMetadata(METADATA_KEY, METADATA_VALUE) }
            assertThrows<InterruptedException> { trace.close() }
        }
    }
}
