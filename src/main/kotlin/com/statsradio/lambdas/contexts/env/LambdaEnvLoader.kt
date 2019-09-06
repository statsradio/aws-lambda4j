package com.statsradio.lambdas.contexts.env

import org.apache.logging.log4j.kotlin.Logging

class CouldNotLoadEnvException(env: String, error: Throwable?) :
    IllegalArgumentException("Could not load parameter $env", error) {

    constructor(env: String) : this(env, null)
}

/**
 * Simple environment loading class
 */
interface LambdaEnvLoader {

    companion object : Logging

    /**
     * Loads a required environment variable
     *
     * @param env Variable name
     * @throws CouldNotLoadEnvException if the variable does not exist
     */
    @Throws(CouldNotLoadEnvException::class)
    fun load(env: String): String

    /**
     * Loads an optional environment variable
     *
     * @param env Variable name
     * @return Value if present or null
     */
    fun tryLoad(env: String): String? {
        return try {
            load(env)
        } catch (error: CouldNotLoadEnvException) {
            logger.info("Could not load $env from env loader(s), fallback to null", error)
            null
        }
    }
}
