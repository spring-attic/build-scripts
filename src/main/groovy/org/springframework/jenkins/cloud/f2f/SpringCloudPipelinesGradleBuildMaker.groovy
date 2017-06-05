package org.springframework.jenkins.cloud.f2f

import org.springframework.jenkins.cloud.common.SpringCloudJobs
import org.springframework.jenkins.common.job.JdkConfig
import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.cloud.common.SpringCloudNotification
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.TestPublisher

/**
 * @author Marcin Grzejszczak
 */
class SpringCloudPipelinesGradleBuildMaker implements TestPublisher, JdkConfig, Cron, SpringCloudJobs {
	private final DslFactory dsl
	private final String githubOrg = 'spring-cloud-samples'

	SpringCloudPipelinesGradleBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void build(String projectName) {
		build(projectName, oncePerDay())
	}

	void build(String projectName, String cronExpr) {
		dsl.job("spring-cloud-${projectName}-f2f") {
			triggers {
				githubPush()
				cron cronExpr
			}
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/$githubOrg/$projectName"
						branch 'master'
					}
				}
			}
			steps {
				shell("""
				set +x
				./gradlew clean build -PnewVersion=0.0.1.M1
				set -x
				""")
			}
			configure {
				SpringCloudNotification.cloudSlack(it as Node)
			}
			publishers {
				archiveJunit gradleJUnitResults()
			}
		}
	}
}
