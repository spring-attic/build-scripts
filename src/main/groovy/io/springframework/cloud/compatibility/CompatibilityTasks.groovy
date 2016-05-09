package io.springframework.cloud.compatibility

import groovy.transform.PackageScope
import javaposse.jobdsl.dsl.helpers.step.StepContext

/**
 * @author Marcin Grzejszczak
 */
@PackageScope
abstract class CompatibilityTasks {

	private static final String springBootVersion = '1.3.5.BUILD-SNAPSHOT'

	Closure defaultSteps() {
		return buildStep {
			shell runTests()
			shell("""
					echo -e "Printing the list of dependencies"
					./mvnw dependency:tree -U -Dspring-boot.version=${springBootVersion}
					""")
		}
	}

	protected String runTests() {
		return """
					echo -e "Running the tests"
					./mvnw clean install -U -fae -Dspring-boot.version=${springBootVersion}"""
	}

	private Closure buildStep(@DelegatesTo(StepContext) Closure buildSteps) {
		return buildSteps
	}

}
