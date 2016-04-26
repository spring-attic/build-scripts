package io.springframework.cloud.e2e

/**
 * @author Marcin Grzejszczak
 */
trait BreweryDefatuts {
	String acceptanceTestReports() {
		return 'target/brewery/acceptance-tests/build/reports/tests/**/*.*'
	}

	String acceptanceTestSpockReports() {
		return 'target/brewery/acceptance-tests/build/spock-reports/**/*'
	}
}