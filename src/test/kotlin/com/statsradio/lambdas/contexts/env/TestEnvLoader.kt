package com.statsradio.lambdas.contexts.env

class TestEnvLoader(
    private val env: Map<String, String>
) : LambdaEnvLoader {

    override fun load(env: String): String {
        return this.env[env] ?: throw CouldNotLoadEnvException("Could not find $env")
    }
}
