package io.springframework.cloud.compatibility

import io.springframework.common.JdkConfig
import io.springframework.common.Notification
import io.springframework.common.Publisher
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class CompatibilityBuildMaker extends CompatibilityTasks implements Notification, Publisher, JdkConfig {
	private final DslFactory dsl

	CompatibilityBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void build(String projectName, String cronExpr) {
		dsl.job("${projectName}-compatibility-check") {
			triggers {
				cron cronExpr
			}
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/spring-cloud/$projectName"
						branch 'master'
					}
					createTag(false)
				}
			}
			steps defaultSteps()
			configure {
				appendSlackNotificationForSpringCloud(it as Node)
			}
			publishers {
				archiveJunit mavenJunitResults()
			}
		}
	}

}
