package com.statsradio.lambdas.contexts.env

/**
 * Chains multiple EnvLoader together (fallbacks) if the requested variable is not present
 */
class MultipleEnvLoaders(
    private val loaders: List<LambdaEnvLoader>
) : LambdaEnvLoader {

    override fun load(env: String): String {
        return loaders.fold<LambdaEnvLoader, String?>(
            null,
            { acc, loader -> acc ?: loader.tryLoad(env) }
        ) ?: throw CouldNotLoadEnvException(env)
    }
}
