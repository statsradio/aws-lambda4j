package com.statsradio.lambdas.contexts

import com.statsradio.lambdas.contexts.env.LambdaEnvLoader
import com.statsradio.lambdas.contexts.env.LambdaEnvLoaderFactory
import com.statsradio.lambdas.logging.LambdaDefaultLogger
import com.statsradio.lambdas.logging.LambdaLogger
import com.statsradio.lambdas.logging.LambdaMultipleLogger
import com.statsradio.lambdas.logging.LambdaSentryLogger
import com.statsradio.lambdas.tracing.LambdaTracingFactory
import com.statsradio.tracing.TracerFactory
import java.time.Clock

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
    private val clock: Clock = Clock.systemUTC(),
    private val envLoader: LambdaEnvLoader = LambdaEnvLoaderFactory().create()
) {
    /**
     * Lambda lifecycle logger, configured with Log4J2 and Sentry if `SENTRY_DSN` is present
     */
    val logger by lazy { configureLogger() }

    /**
     * Lambda tracing factory, configured with Sentry and XRAY if `SENTRY_DSN` and `XRAY_TRACING_ENABLED` is setup
     */
    open val tracingFactory by lazy { LambdaTracingFactory(envLoader, TracerFactory(clock)) }

    val tracer by lazy { tracingFactory.createTracer() }

    /**
     * Overridable lambda logger configuration
     */
    protected open fun configureLogger(): LambdaLogger {
        val tracers = mutableListOf<LambdaLogger>()

        envLoader.tryLoad("SENTRY_DSN")?.apply {
            val sentryTracer = configureSentryLogger(this)
            tracers.add(sentryTracer)
        }

        tracers.add(LambdaDefaultLogger())
        return LambdaMultipleLogger(tracers)
    }

    private fun configureSentryLogger(dsn: String): LambdaSentryLogger {
        val env = envLoader.tryLoad("ENV") ?: DefaultConfiguration.ENV
        return LambdaSentryLogger.configureDefault(dsn, env)
    }
}
