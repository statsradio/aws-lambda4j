[![Build Status](https://travis-ci.org/statsradio/aws-lambda4j.svg?branch=master)](https://travis-ci.org/statsradio/aws-lambda4j)
# AWS Lambda4J
Small utility classes used to facilitate Lambda development with Kotlin

## Getting started
The library is currently hosted on GitHub packages

### pom.xml
```
<dependency>
    <groupId>com.statsradio.lambdas</groupId>
    <artifactId>aws-lambda4j</artifactId>
    <version>0.0.1</version>
</dependency>
```

### Maven Repository
Add this line and GitHub authentication to your `.m2/settings.xml` file
```
<repository>
  <id>github</id>
  <name>GitHub StatsRadio Apache Maven Packages</name>
  <url>https://maven.pkg.github.com/statsradio/aws-lambda4j</url>
</repository>
```
More information: https://help.github.com/en/github/managing-packages-with-github-packages/configuring-apache-maven-for-use-with-github-packages

## Handler
The provided abstract handler class implements logging of request/response and error handling for your lambda handlers.
```kotlin
class MyHandler : LambdaHandler<String, String>() {
    override fun handleRequest(input: String, awsRuntimeContext: Context) {
        return "Test: $input"
    }
}
```

## Context
The `LambdaContext` has a default `EnvLoader` using SSM and local environment variable.
Use it to manage your dependencies.
```kotlin
class MyContext : LambdaContext() {
    val notifier: MyNotifier by lazy {
        val snsTopicArn = envLoader.load("SNS_TOPIC_ARN")
        MySnsNotifier(snsTopicArn)
    }
}

class MyHandler(context: MyContext = MyContext()) : LambdaHandler<String, Unit>(context) {
    
    private val notifier = context.notifier

    override fun handleRequest(input: String, awsRuntimeContext: Context) {
        notifier.notify(message = input)
    }
}
```

## Environment
`EnvLoader` is an interface to simply load variable from various sources.
```kotlin
// Included
val ssm = SsmEnvLoader(ssmPath = "<prefix>", ssm = AWSSimpleSystemsManagementClientBuilder.defaultClient())
val local = LocalEnvLoader()

MultipleEnvLoader(listOf(local, ssm))
```

By default the `LambdaContext` uses a `MultipleEnvLoader` with `LocalEnvLoader => SsmEnvLoader`

## Logging
By default, the `LambdaContext` provides a `Logger` with Sentry and Log4J2 integrations.

### LambdaDefaultLogger (Log4J2)
* See `log4j2.xml` file in the resource for sample configuration
* https://docs.aws.amazon.com/lambda/latest/dg/java-logging.html

### LambdaSentryLogger
* Requires `SENTRY_DSN` from your env-loader
* See `log4j2.xml` for lambda integration
* See `sentry.properties` for lambda integration (disable sentry client asynchronicity)
