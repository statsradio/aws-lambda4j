package com.statsradio.lambdas.contexts.env

import assertk.assertThat
import assertk.assertions.isNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LocalEnvLoaderTest {

    companion object {
        const val EXISTING_ENV_VAL = "PATH"
        const val NOT_EXISTING_ENV_VARIABLE = "kjbhvhsdjkdsnjlgkjgljacdbknvSJBHNJGF"
    }

    private val localEnvLoader = LocalEnvLoader()

    @Test
    fun `should find environment variable`() {
        val env = localEnvLoader.load(EXISTING_ENV_VAL)
        assertThat(env).isNotNull()
    }

    @Test
    fun `given not existing environment variable, should throw exception`() {
        assertThrows<CouldNotLoadEnvException> { localEnvLoader.load(NOT_EXISTING_ENV_VARIABLE) }
    }
}
