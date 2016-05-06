package io.springframework.cloud.ci

import io.springframework.common.JdkConfig
import io.springframework.common.Notification
import io.springframework.common.Publisher
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class SpringCloudDeployBuildMaker implements Notification, JdkConfig, Publisher {
	private final DslFactory dsl

	SpringCloudDeployBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void deploy(String project, boolean checkTests = true) {
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
					wipeOutWorkspace()
					createTag(false)
				}
			}
			steps {
				shell('''
					echo "Clearing the installed cloud artifacts"
					rm -rf ~/.m2/repository/org/springframework/cloud/
					''')
				shell('''
					./mvnw clean install -P docs -q -U -DskipTests=true -Dmaven.test.redirectTestOutputToFile=true
					./docs/src/main/asciidoc/ghpages.sh
					''')
				shell('''
					git reset --hard && git checkout master && git pull origin master &&
					./mvnw clean deploy -nsu -Dmaven.test.redirectTestOutputToFile=true
					''')
			}
			configure {
				appendSlackNotificationForSpringCloud(it as Node)
			}
			if (checkTests) {
				publishers {
					archiveJunit mavenJunitResults()
				}
			}
		}
	}

	void deployWithoutTests(String project) {
		deploy(project, false)
	}
}
