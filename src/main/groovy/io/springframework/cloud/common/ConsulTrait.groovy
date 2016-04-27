package io.springframework.cloud.common

/**
 * @author Marcin Grzejszczak
 */
trait ConsulTrait {

	String preConsulShell() {
		return '''
					echo "Clearing consul data"
					rm -rf /tmp/consul
					rm -rf /tmp/consul-config

					echo "Install consul"
					./src/main/bash/travis_install_consul.sh

					echo "Run consul"
					./src/test/bash/travis_run_consul.sh
				'''
	}

	String postConsulShell() {
		return '''
					echo "Kill consul"
					kill -9 $(ps aux | grep '[c]onsul' | awk '{print $2}') && echo "Killed consul" || echo "Can't find consul in running processes"
					'''
	}
}