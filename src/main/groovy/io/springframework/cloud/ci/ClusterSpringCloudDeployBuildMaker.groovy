package io.springframework.cloud.ci

import io.springframework.cloud.common.ClusterTrait
import io.springframework.common.JdkConfig
import io.springframework.common.Notification
import io.springframework.common.Publisher
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class ClusterSpringCloudDeployBuildMaker implements Notification, JdkConfig, Publisher, ClusterTrait {
	private final DslFactory dsl

	ClusterSpringCloudDeployBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void deploy() {
		String project = 'spring-cloud-cluster'
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

				}
			}
			steps {
				shell('''
						echo "Clearing the installed cloud artifacts"
						rm -rf ~/.m2/repository/org/springframework/cloud/
						''')
				shell('''
						./mvnw install -P docs -q -U -DskipTests=true -Dmaven.test.redirectTestOutputToFile=true
						./docs/src/main/asciidoc/ghpages.sh
						git reset --hard && git checkout master
					''')
				shell("""
						${preClusterShell()}
						./mvnw clean deploy -nsu -Dmaven.test.redirectTestOutputToFile=true || ${postClusterShell()}
					""")
				shell postClusterShell()
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
