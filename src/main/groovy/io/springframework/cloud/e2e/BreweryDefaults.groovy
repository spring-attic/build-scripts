package io.springframework.cloud.e2e

/**
 * @author Marcin Grzejszczak
 */
trait BreweryDefaults {
	String killAllApps() {
		return '''
			function kill_all_apps() {
				kill_app_with_port 9991
				kill_app_with_port 9992
				kill_app_with_port 9993
				kill_app_with_port 9994
				kill_app_with_port 9995
				kill_app_with_port 9996
				kill_app_with_port 9997
				kill_app_with_port 9998
				kill_app_with_port 9999
				kill_app_with_port 8888
				kill_app_with_port 8761
				kill_app_with_port 9411
				kill_app_with_port 9092
				kill_app_with_port 2181
				docker kill $(docker ps -a -q) || echo "No running docker containers are left"
                docker stop `docker ps -a -q --filter="image=spotify/kafka"` || echo "No docker with Kafka was running - won't stop anything"
                docker kill `docker ps -a -q --filter="image=spotify/kafka"` || echo "No docker with Kafka was running - won't stop anything"
			}

			# port is $1
			function kill_app_with_port() {
				kill -9 $(lsof -t -i:$1) && echo "Killed an app running on port [$1]" || echo "No app running on port [$1]"
			}

			kill_all_apps
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