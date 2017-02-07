package io.springframework.cloud.compatibility

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import io.springframework.cloud.common.AllCloudConstants
import javaposse.jobdsl.dsl.helpers.step.StepContext

/**
 * @author Marcin Grzejszczak
 */
@PackageScope
@CompileStatic
abstract class CompatibilityTasks {

	protected static final String DEFAULT_BOOT_VERSION = AllCloudConstants.DEFAULT_BOOT_VERSION
	protected static final String SPRING_BOOT_VERSION_VAR = 'SPRING_BOOT_VERSION'

	Closure defaultSteps() {
		return buildStep {
			shell runTests()
			shell("""
					echo -e "Printing the list of dependencies"
					./mvnw dependency:tree -U -Dspring-boot.version=\$${SPRING_BOOT_VERSION_VAR}
					""")
		}
	}

	protected String runTests() {
		return """
					echo -e "Checking if prod code compiles against latest boot"
					./mvnw clean compile -U -fae -Dspring-boot.version=\$${SPRING_BOOT_VERSION_VAR}"""
	}

	private Closure buildStep(@DelegatesTo(StepContext) Closure buildSteps) {
		return buildSteps
	}

}
