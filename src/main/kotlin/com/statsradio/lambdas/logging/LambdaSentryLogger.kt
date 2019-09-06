package com.statsradio.lambdas.logging

import com.amazonaws.services.lambda.runtime.Context
import io.sentry.SentryClient
import io.sentry.event.Breadcrumb
import io.sentry.event.BreadcrumbBuilder

/**
 * Sentry logger for the lambda lifecycle
 *
 * Records breadcrumbs and extras akin to the Python integration
 * Limitations : relies on the Log4J2 integration to actually send errors/warnings to Sentry
 */
class LambdaSentryLogger(
    sentryClient: SentryClient
) : LambdaLogger {

    private val sentryContext = sentryClient.context

    override fun recordError(error: Exception, awsRuntimeContext: Context) {
        val breadcrumb = BreadcrumbBuilder()
            .setCategory("error")
            .setLevel(Breadcrumb.Level.ERROR)
            .setMessage(error.message)
            .let { builder -> buildBreadcrumb(builder, awsRuntimeContext) }

        sentryContext.recordBreadcrumb(breadcrumb)
        sentryContext.addExtra("error", error)
    }

    override fun recordResponse(response: Any, awsRuntimeContext: Context) {
        val breadcrumb = BreadcrumbBuilder()
            .setCategory("response")
            .setLevel(Breadcrumb.Level.INFO)
            .withData("content", response.toString())
            .let { builder -> buildBreadcrumb(builder, awsRuntimeContext) }

        sentryContext.recordBreadcrumb(breadcrumb)
        sentryContext.addExtra("response", response)
    }

    override fun recordRequest(request: Any, awsRuntimeContext: Context) {
        val breadcrumb = BreadcrumbBuilder()
            .setCategory("request")
            .setLevel(Breadcrumb.Level.INFO)
            .withData("content", request.toString())
            .let { builder -> buildBreadcrumb(builder, awsRuntimeContext) }

        sentryContext.addExtra("request_id", awsRuntimeContext.awsRequestId)
        sentryContext.addExtra("function_name", awsRuntimeContext.functionName)
        sentryContext.addExtra("function_version", awsRuntimeContext.functionVersion)
        sentryContext.addExtra("memory_limit", awsRuntimeContext.memoryLimitInMB)
        sentryContext.addExtra("maximum_execution_time", awsRuntimeContext.remainingTimeInMillis)

        sentryContext.recordBreadcrumb(breadcrumb)
        sentryContext.addExtra("request", request)
    }

    override fun recordEvent(type: String, message: String, metadata: Map<String, String>) {
        val breadcrumb = BreadcrumbBuilder()
            .setCategory(type)
            .setMessage(message)
            .setLevel(Breadcrumb.Level.INFO)
            .setData(metadata)
            .build()

        sentryContext.recordBreadcrumb(breadcrumb)
    }

    private fun buildBreadcrumb(builder: BreadcrumbBuilder, awsRuntimeContext: Context): Breadcrumb {
        builder.withData("request_id", awsRuntimeContext.awsRequestId)
        builder.withData("time_remaining", "${awsRuntimeContext.remainingTimeInMillis}ms")
        return builder.build()
    }
}
