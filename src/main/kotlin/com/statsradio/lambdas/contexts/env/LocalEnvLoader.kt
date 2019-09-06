package com.statsradio.lambdas.contexts.env

/**
 * Loads from the local environment variables
 */
class LocalEnvLoader : LambdaEnvLoader {

    override fun load(env: String): String {
        return System.getenv(env) ?: throw CouldNotLoadEnvException(env)
    }
}
