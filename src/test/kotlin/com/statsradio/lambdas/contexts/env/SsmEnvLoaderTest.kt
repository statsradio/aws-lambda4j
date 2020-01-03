package com.statsradio.lambdas.contexts.env

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension

class SsmEnvLoaderTest {

    companion object {
        @JvmField
        @RegisterExtension
        val ssmPath = SsmPathExtension()
    }

    private val ssmEnvLoader = SsmEnvLoader(ssmPath = ssmPath.ssmPath, ssm = ssmPath.ssm)

    @Test
    fun `should get parameter from ssm`() {
        ssmPath.add("param", "with value")
        val value = ssmEnvLoader.load("param")
        assertThat(value).isEqualTo("with value")
    }

    @Test
    fun `given no parameter, should throw exception`() {
        assertThrows<CouldNotLoadEnvException> { ssmEnvLoader.load("noparam") }
    }

    @Test
    fun `given encrypted parameter, should load from ssm`() {
        ssmPath.add("encrypted_param", "encrypted value", encrypted = true)
        val value = ssmEnvLoader.load("encrypted_param")
        assertThat(value).isEqualTo("encrypted value")
    }
}
