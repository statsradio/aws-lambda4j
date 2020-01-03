SOURCE_FILES := $(shell find src -type f)

.PHONY: lint test package

lint:
	mvn antrun:run@detekt

test:
	mvn test

package: target/aws-lambda4j.jar
target/aws-lambda4j.jar: pom.xml $(SOURCE_FILES)
	mvn package -Dmaven.test.skip=true
