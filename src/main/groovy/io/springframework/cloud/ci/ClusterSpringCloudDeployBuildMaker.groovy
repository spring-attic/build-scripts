package io.springframework.cloud.ci

import io.springframework.cloud.common.ClusterTrait
import io.springframework.cloud.common.SpringCloudJobs
import io.springframework.common.JdkConfig
import io.springframework.common.Notification
import io.springframework.common.Publisher
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class ClusterSpringCloudDeployBuildMaker implements Notification, JdkConfig, Publisher,
		ClusterTrait, SpringCloudJobs {
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
				shell(cleanup())
				shell(buildDocsWithGhPages())
				shell("""
						${preClusterShell()}
						${cleanAndDeploy()} || ${postClusterShell()}
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
