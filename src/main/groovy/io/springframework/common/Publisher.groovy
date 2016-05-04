package io.springframework.common
/**
 * @author Marcin Grzejszczak
 */
trait Publisher {
	String mavenJunitResults() {
		return '**/surefire-reports/*.xml'
	}

	String gradleJUnitResults() {
		return '**/test-results/*.xml'
	}
}
