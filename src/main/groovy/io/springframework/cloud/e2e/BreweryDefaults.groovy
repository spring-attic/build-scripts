package io.springframework.cloud.e2e

/**
 * @author Marcin Grzejszczak
 */
trait BreweryDefaults {
	String killAllApps() {
		return '''
			./runAcceptanceTests.sh -n
		'''
	}

	String acceptanceTestReports() {
		return '**/acceptance-tests/build/reports/tests/**/*.*'
	}

	String acceptanceTestSpockReports() {
		return '**/acceptance-tests/build/spock-reports/**/*'
	}

	int defaultInactivity() {
		return 30 * 60
	}
}