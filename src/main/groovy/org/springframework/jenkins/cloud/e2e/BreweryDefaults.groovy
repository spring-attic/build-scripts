package org.springframework.jenkins.cloud.e2e

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
				docker --tlsverify=false stop $(docker --tlsverify=false ps -a -q) || echo "No docker containers where active"
				docker --tlsverify=false kill $(docker --tlsverify=false ps -a -q) || echo "Nothing to kill"
				#docker --tlsverify=false rm $(docker --tlsverify=false ps -a -q) 
				#docker --tlsverify=false rmi $(docker --tlsverify=false images -q) -f
				#docker volume ls -qf dangling=true | xargs -r docker volume rm
				#docker volume rm $(docker volume ls -qf dangling=true)
				pkill -f zookeeper || echo "No zookeeper process was running"
				pkill -f kafka || echo "No kafka process was running"
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