package io.springframework.cloud.e2e

/**
 * @author Marcin Grzejszczak
 */
trait BreweryDefatuts {
	String acceptanceTestReports() {
		return '**/acceptance-tests/build/reports/tests/**/*.*'
	}

	String acceptanceTestSpockReports() {
		return '**/acceptance-tests/build/spock-reports/**/*'
	}
}