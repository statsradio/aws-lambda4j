SOURCE_FILES := $(shell find src -type f)

.PHONY: build lint test package publish

build:
	mvn compile -DskipTests

lint:
	mvn antrun:run@detekt

test:
	mvn test

package: target/aws-lambda4j.jar
target/aws-lambda4j.jar: pom.xml $(SOURCE_FILES)
	mvn package -Dmaven.test.skip=true

publish: target/aws-lambda4j.jar
	mvn deploy -Dmaven.test.skip=true
