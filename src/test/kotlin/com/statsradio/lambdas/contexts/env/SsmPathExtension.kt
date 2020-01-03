package com.statsradio.lambdas.contexts.env

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder
import com.amazonaws.services.simplesystemsmanagement.model.DeleteParametersRequest
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathRequest
import com.amazonaws.services.simplesystemsmanagement.model.ParameterType
import com.amazonaws.services.simplesystemsmanagement.model.PutParameterRequest
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.util.*

class SsmPathExtension : AfterAllCallback {

    val ssm = AWSSimpleSystemsManagementClientBuilder.defaultClient()!!
    val ssmPath by lazy { "/lambda4j-test-${UUID.randomUUID()}" }

    fun add(name: String, value: String, encrypted: Boolean = false) {
        with(PutParameterRequest().withName("$ssmPath/$name").withValue(value)) {
            type = when (encrypted) {
                true -> ParameterType.SecureString.toString()
                false -> ParameterType.String.toString()
            }

            ssm.putParameter(this)
        }
    }

    override fun afterAll(ctx: ExtensionContext?) {
        val parameters = ssm.getParametersByPath(GetParametersByPathRequest().withPath(ssmPath))
            .parameters.map { p -> p.name }

        if (parameters.isEmpty()) {
            return
        }

        ssm.deleteParameters(DeleteParametersRequest().withNames(parameters))
    }
}
