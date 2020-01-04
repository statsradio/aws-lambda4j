SOURCE_FILES := $(shell find src -type f)

.PHONY: lint test package publish

lint:
	mvn antrun:run@detekt

test:
	mvn test

package: target/aws-lambda4j.jar
target/aws-lambda4j.jar: pom.xml $(SOURCE_FILES)
	mvn package -Dmaven.test.skip=true

publish: target/aws-lambda4j.jar
	mvn deploy -Dmaven.test.skip=true
