package com.statsradio.lambdas.contexts.env

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MultipleEnvLoadersTest {

    companion object {
        private const val ENV = "env"
        private const val VALUE_1 = "value1"
        private const val VALUE_2 = "value2"
    }

    private val badLoader = mockk<LambdaEnvLoader>()
    private val goodLoader1 = mockk<LambdaEnvLoader>()
    private val goodLoader2 = mockk<LambdaEnvLoader>()

    @BeforeEach
    fun setUp() {
        clearMocks(badLoader, goodLoader1, goodLoader2)

        every {
            badLoader.tryLoad(ENV)
            goodLoader1.tryLoad(ENV)
            goodLoader2.tryLoad(ENV)
        } answers { callOriginal() }

        every {
            goodLoader1.load(ENV)
            goodLoader1.tryLoad(ENV)
        } returns VALUE_1

        every {
            goodLoader2.load(ENV)
            goodLoader2.tryLoad(ENV)
        } returns VALUE_2

        every { badLoader.tryLoad(ENV) } returns null
        every { badLoader.load(ENV) } throws CouldNotLoadEnvException(ENV)
    }

    @Test
    fun `should use the first good loader`() {
        val loaders = MultipleEnvLoaders(listOf(badLoader, goodLoader2))

        val env = loaders.load(ENV)

        assertThat(env).isEqualTo(VALUE_2)
    }

    @Test
    fun `given only bad loaders, should throw exception`() {
        val loaders = MultipleEnvLoaders(listOf(badLoader, badLoader, badLoader))

        assertThrows<CouldNotLoadEnvException> { loaders.load(ENV) }
    }

    @Test
    fun `given only good loaders, should use the first one`() {
        val loaders = MultipleEnvLoaders(listOf(goodLoader1, goodLoader2))

        val env = loaders.load(ENV)

        assertThat(env).isEqualTo(VALUE_1)
    }
}
