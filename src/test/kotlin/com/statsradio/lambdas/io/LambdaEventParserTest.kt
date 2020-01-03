package com.statsradio.lambdas.io

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LambdaEventParserTest {

    @Test
    fun `given present value, when field is required, should return value`() {
        val value: String? = "a value"

        val parsedValue = value.fieldRequired("field")

        assertThat(parsedValue).isEqualTo("a value")
    }

    @Test
    fun `given null value, when field is required, should throw exception`() {
        val value: String? = null
        assertThrows<LambdaEventParsingError> { value.fieldRequired("field") }
    }
}
