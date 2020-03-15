package com.statsradio.lambdas.contexts

import com.amazonaws.xray.AWSXRay
import com.statsradio.lambdas.contexts.env.LambdaEnvLoader
import com.statsradio.lambdas.contexts.env.LambdaEnvLoaderFactory
import com.statsradio.lambdas.logging.LambdaDefaultLogger
import com.statsradio.lambdas.logging.LambdaLogger
import com.statsradio.lambdas.logging.LambdaMultipleLogger
import com.statsradio.lambdas.logging.LambdaSentryLogger
import com.statsradio.tracing.Tracer
import com.statsradio.tracing.log4j.Log4JTracer
import com.statsradio.tracing.xray.XRayTracer

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
    val logger by lazy { configureLogger() }

    val tracing by lazy { configureTracer() }

    /**
     * Overridable tracing configuration
     */
    protected open fun configureTracer(): Tracer {
        val enableXRayTracing = envLoader.tryLoad("XRAY_TRACING")?.toBoolean() ?: false
        if (enableXRayTracing) {
            return Log4JTracer()
        }

        System.setProperty("com.amazonaws.xray.strategy.contextMissingStrategy", "LOG_ERROR")

        val recorder = AWSXRay.getGlobalRecorder()
        return XRayTracer(recorder)
    }

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
