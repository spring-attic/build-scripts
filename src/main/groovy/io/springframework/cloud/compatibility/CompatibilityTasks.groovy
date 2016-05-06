package io.springframework.cloud.compatibility

import groovy.transform.PackageScope
import javaposse.jobdsl.dsl.helpers.step.StepContext

/**
 * @author Marcin Grzejszczak
 */
@PackageScope
abstract class CompatibilityTasks {

	Closure defaultSteps() {
		return buildStep {
			shell("""
					echo -e "Printing the list of dependencies"
					./mvnw dependency:tree -U -Dspring-boot.version=1.4.0.BUILD-SNAPSHOT
					""")
			shell runTests()
		}
	}

	protected String runTests() {
		return '''
					echo -e "Running the tests"
					./mvnw clean install
					./mvnw verify -U -fae -Dspring-boot.version=1.4.0.BUILD-SNAPSHOT'''
	}

	private Closure buildStep(@DelegatesTo(StepContext) Closure buildSteps) {
		return buildSteps
	}

}
