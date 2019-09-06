package com.statsradio.lambdas.contexts

import com.statsradio.lambdas.contexts.env.LambdaEnvLoader
import com.statsradio.lambdas.contexts.env.LambdaEnvLoaderFactory
import com.statsradio.lambdas.logging.LambdaDefaultLogger
import com.statsradio.lambdas.logging.LambdaLogger
import com.statsradio.lambdas.logging.LambdaMultipleLogger
import com.statsradio.lambdas.logging.LambdaSentryLogger
import io.sentry.Sentry

object DefaultConfiguration {
    const val ENV = "test"
}

/**
 * Extensible Lambda Context
 *
 * Comes with a LocalEnv => Ssm EnvLoader by default
 *
 * @param envLoader Overrides the default EnvLoader
 */
open class LambdaContext(
    private val envLoader: LambdaEnvLoader = LambdaEnvLoaderFactory().create()
) {
    /**
     * Lambda lifecycle logger, configured with Log4J2 and Sentry if `SENTRY_DSN` is present
     */
    val logger: LambdaLogger by lazy {
        val tracers = mutableListOf<LambdaLogger>()

        envLoader.tryLoad("SENTRY_DSN")?.apply {
            val sentryTracer = createSentryLogger(this)
            tracers.add(sentryTracer)
        }

        tracers.add(LambdaDefaultLogger())
        LambdaMultipleLogger(tracers)
    }

    private fun createSentryLogger(dsn: String): LambdaSentryLogger {
        val sentry = Sentry.init(dsn)
        sentry.environment = envLoader.tryLoad("ENV") ?: DefaultConfiguration.ENV

        return LambdaSentryLogger(sentry)
    }
}
