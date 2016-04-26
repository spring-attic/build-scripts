package io.springframework.cloud.compatibility

import io.springframework.common.PublisherTrait
import javaposse.jobdsl.dsl.DslFactory
/**
 * @author Marcin Grzejszczak
 */
class ConsulCompatibilityBuildMaker extends CompatibilityTasks implements PublisherTrait {
	private final DslFactory dsl

	ConsulCompatibilityBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void build(String projectName, String cronExpr = "0 0 0 1/1 * ? *") {
		dsl.job("${projectName}-compatibility-check") {
			triggers {
				cron cronExpr
			}
			scm {
				git {
					remote {
						url "https://github.com/spring-cloud/$projectName"
					}
					createTag(false)
				}
			}
			steps {
				shell('''
					echo "Clearing consul data"
					rm -rf /tmp/consul
					rm -rf /tmp/consul-config
					''')
				shell('''
					echo "Install consul"
					./src/main/bash/travis_install_consul.sh

					echo "Run consul"
					./src/test/bash/travis_run_consul.sh
				''')

			}
			steps defaultSteps()
			steps {
				shell('''
					echo "Kill consul"
					kill -9 $(ps aux | grep '[c]onsul' | awk '{print $2}') && echo "Killed consul" || echo "Can't find consul in running processes"
					''')
			}
			publishers {
				archiveJunit mavenJunitResults()
			}
		}
	}
}
