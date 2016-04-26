package io.springframework.cloud.compatibility

import groovy.transform.PackageScope
import javaposse.jobdsl.dsl.helpers.step.StepContext

/**
 * @author Marcin Grzejszczak
 */
@PackageScope
abstract class CompatibilityTasks {

	Closure defaultSteps() {
		def springCloudBuildUrl = 'https://github.com/spring-cloud/spring-cloud-build'
		def springBootVersion = '1.4.0.BUILD-SNAPSHOT'
		def gistUrl = 'https://gist.githubusercontent.com/marcingrzejszczak/e63d4985f2a12d51af3310be51b2caa2/raw/c741bfea548b2b99fc242d743b1270301eab5167/replace_parent_version_in_pom.groovy'
		return buildStep {
			shell("""
					echo "Removing spring-cloud-build if present"
					rm -rf spring-cloud-build
					rm -rf replace_parent_version_in_pom.*
					echo "Cloning spring-cloud-build"
					git clone $springCloudBuildUrl
					echo "Downloading and running script to change parent version"
					wget $gistUrl --no-check-certificate
					""".toString())
			groovyScriptFile('replace_parent_version_in_pom.groovy ') {
				scriptParams(["-p 'spring-cloud-build/pom.xml'", "-v '$springBootVersion'"])
				groovyInstallation('Default')
			}
			shell('''
					echo "Installing built version with different parent"
					./spring-cloud-build/mvnw clean install
					''')
			shell("""
					echo -e "Printing the list of dependencies"
					./mvnw dependency:tree
					""")
			shell('''
					echo -e "Running the tests"
					./mvnw clean verify -fae
					''')
		}
	}

	private Closure buildStep(@DelegatesTo(StepContext) Closure buildSteps) {
		return buildSteps
	}

}
