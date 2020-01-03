package com.statsradio.lambdas.contexts.env

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement
import com.amazonaws.services.simplesystemsmanagement.model.AWSSimpleSystemsManagementException
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest

/**
 * Loads and decrypts variables from SSM Parameter Store Path
 * Make sure that your environment has the required IAM policies to access requested variables
 *
 * @param ssmPath prefix to the SSM configuration (ex.: "myproject/staging")
 * @param ssm SSM client
 */
class SsmEnvLoader(
    private val ssmPath: String,
    private val ssm: AWSSimpleSystemsManagement
) : LambdaEnvLoader {

    override fun load(env: String): String {
        with(GetParameterRequest()) {
            name = "$ssmPath/$env"
            withDecryption = true

            return load(env, this)
        }
    }

    private fun load(env: String, request: GetParameterRequest): String {
        return try {
            ssm.getParameter(request).parameter.value
        } catch (error: AWSSimpleSystemsManagementException) {
            throw CouldNotLoadEnvException(env, error)
        }
    }
}
