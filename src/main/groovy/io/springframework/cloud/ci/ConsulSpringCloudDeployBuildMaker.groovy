package io.springframework.cloud.ci

import io.springframework.cloud.common.ConsulTrait
import io.springframework.common.JdkConfig
import io.springframework.common.Notification
import io.springframework.common.Publisher
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class ConsulSpringCloudDeployBuildMaker implements Notification, JdkConfig, Publisher, ConsulTrait {
	private final DslFactory dsl

	ConsulSpringCloudDeployBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void deploy() {
		String project = 'spring-cloud-consul'
		dsl.job("$project-ci") {
			triggers {
				githubPush()
			}
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/spring-cloud/${project}"
						branch 'master'
					}
					createTag(false)
				}
			}
			steps {
				shell('''
					./mvnw install -P docs -q -U -DskipTests=true -Dmaven.test.redirectTestOutputToFile=true
					./docs/src/main/asciidoc/ghpages.sh
					git reset --hard && git checkout master
					''')
				shell preConsulShell()
				shell('''
					./mvnw clean deploy -nsu -Dmaven.test.redirectTestOutputToFile=true
					''')
				shell postConsulShell()
			}
			configure {
				appendSlackNotificationForSpringCloud(it as Node)
			}
			publishers {
				archiveJunit mavenJunitResults()
			}
		}
	}
}
