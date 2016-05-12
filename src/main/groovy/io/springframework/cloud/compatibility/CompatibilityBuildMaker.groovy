package io.springframework.cloud.compatibility

import io.springframework.common.JdkConfig
import io.springframework.common.Notification
import io.springframework.common.Publisher
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class CompatibilityBuildMaker extends CompatibilityTasks implements Notification, Publisher, JdkConfig {
	private static final String DEFAULT_BOOT_VERSION = '1.4.0.BUILD-SNAPSHOT'

	private final DslFactory dsl

	CompatibilityBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void build(String projectName, String cronExpr) {
		buildWithTests(projectName, cronExpr, true)
	}

	private void buildWithTests(String projectName, String cronExpr, boolean checkTests) {
		dsl.job("${projectName}-compatibility-check") {
			triggers {
				cron cronExpr
				parameters {
					stringParam(SPRING_BOOT_VERSION_VAR, DEFAULT_BOOT_VERSION, 'Which version of Spring Boot should be used for the build')
				}
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
			if (checkTests) {
				publishers {
					archiveJunit mavenJunitResults()
				}
			}
		}
	}

	void buildWithoutTests(String projectName, String cronExpr) {
		buildWithTests(projectName, cronExpr, false)
	}

}
