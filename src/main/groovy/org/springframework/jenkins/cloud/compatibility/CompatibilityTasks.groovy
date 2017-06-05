package org.springframework.jenkins.cloud.compatibility

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import javaposse.jobdsl.dsl.helpers.step.StepContext
import org.springframework.jenkins.cloud.common.AllCloudConstants

/**
 * @author Marcin Grzejszczak
 */
@PackageScope
@CompileStatic
abstract class CompatibilityTasks {

	protected static final String DEFAULT_BOOT_VERSION = AllCloudConstants.LATEST_BOOT_VERSION
	protected static final String SPRING_BOOT_VERSION_VAR = 'SPRING_BOOT_VERSION'
	protected static final String SPRING_VERSION_VAR = 'SPRING_VERSION'

	Closure defaultStepsForBoot() {
		return buildStep {
			shell compileProductionForBoot()
			shell(printDepsForBoot())
		}
	}

	protected String printDepsForBoot() {
		return """
					echo -e "Printing the list of dependencies"
					./mvnw dependency:tree -U -Dspring-boot.version=\$${SPRING_BOOT_VERSION_VAR}
			"""
	}

	Closure defaultStepsWithTestsForBoot() {
		return buildStep {
			shell runTestsForBoot()
			shell(printDepsForBoot())
		}
	}

	protected String compileProductionForBoot() {
		return """
					echo -e "Checking if prod code compiles against latest boot"
					./mvnw clean compile -U -fae -Dspring-boot.version=\$${SPRING_BOOT_VERSION_VAR}"""
	}

	protected String runTestsForBoot() {
		return """
					echo -e "Checking if prod code compiles against latest boot"
					./mvnw clean install -U -fae -Dspring-boot.version=\$${SPRING_BOOT_VERSION_VAR}"""
	}

	Closure defaultStepsForSpring() {
		return buildStep {
			shell compileProductionForSpring()
			shell(printDepsForSpring())
		}
	}

	protected String printDepsForSpring() {
		return """
					echo -e "Printing the list of dependencies"
					./mvnw dependency:tree -U -Dspring.version=\$${SPRING_VERSION_VAR}
			"""
	}

	Closure defaultStepsWithTestsForSpring() {
		return buildStep {
			shell runTestsForSpring()
			shell(printDepsForSpring())
		}
	}

	protected String compileProductionForSpring() {
		return """
					echo -e "Checking if prod code compiles against latest spring"
					./mvnw clean compile -U -fae -Dspring.version=\$${SPRING_VERSION_VAR}"""
	}

	protected String runTestsForSpring() {
		return """
					echo -e "Checking if prod code compiles against latest spring"
					./mvnw clean install -U -fae -Dspring.version=\$${SPRING_VERSION_VAR}"""
	}

	private Closure buildStep(@DelegatesTo(StepContext) Closure buildSteps) {
		return buildSteps
	}

}
