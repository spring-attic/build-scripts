package io.springframework.cloud.f2f

import io.springframework.cloud.common.SpringCloudNotification
import io.springframework.common.Cron
import io.springframework.common.JdkConfig
import io.springframework.common.SlackPlugin
import io.springframework.common.TestPublisher
import javaposse.jobdsl.dsl.DslFactory
/**
 * @author Marcin Grzejszczak
 */
class SpringCloudPipelinesGradleBuildMaker implements SpringCloudNotification, TestPublisher, JdkConfig, Cron {
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
				shell('''./gradlew clean build deploy -PnewVersion=0.0.1.M1 -DM2_SETTINGS_REPO_ID=repo.spring.io -DREPO_WITH_JARS=https://repo.spring.io/libs-milestone-local''')
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
