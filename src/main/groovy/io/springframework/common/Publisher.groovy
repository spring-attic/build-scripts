package io.springframework.common

/**
 * Contains default patterns for JUnit results
 *
 * @author Marcin Grzejszczak
 */
trait Publisher {
	String mavenJUnitResults() {
		return '**/surefire-reports/*.xml'
	}

	String gradleJUnitResults() {
		return '**/test-results/*.xml'
	}
}
