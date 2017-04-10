package io.springframework.cloud.f2f

import io.springframework.cloud.common.SpringCloudNotification
import io.springframework.common.job.Cron
import io.springframework.common.job.JdkConfig
import io.springframework.common.job.SlackPlugin
import io.springframework.common.job.TestPublisher
import javaposse.jobdsl.dsl.DslFactory
/**
 * @author Marcin Grzejszczak
 */
class SpringCloudPipelinesMavenBuildMaker implements SpringCloudNotification, TestPublisher, JdkConfig, Cron {
	private final DslFactory dsl
	private final String githubOrg = 'spring-cloud-samples'

	SpringCloudPipelinesMavenBuildMaker(DslFactory dsl) {
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
				shell('''./mvnw versions:set -DnewVersion=0.0.1.M1''')
				shell('''./mvnw clean verify install''')
			}
			configure {
				SlackPlugin.slackNotification(it as Node) {
					room(cloudRoom())
				}
			}
			publishers {
				archiveJunit mavenJUnitResults()
			}
		}
	}
}
