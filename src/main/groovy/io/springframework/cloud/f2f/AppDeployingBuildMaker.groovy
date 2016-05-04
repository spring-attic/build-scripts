package io.springframework.cloud.f2f

import io.springframework.common.Cron
import io.springframework.common.JdkConfig
import io.springframework.common.Notification
import io.springframework.common.Publisher
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class AppDeployingBuildMaker implements Notification, Publisher, JdkConfig, Cron {
	private final DslFactory dsl

	AppDeployingBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void build(String githubOrg, String projectName) {
		build(githubOrg, projectName, oncePerDay())
	}

	void build(String githubOrg, String projectName, String cronExpr) {
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
					createTag(false)
				}
			}
			steps {
				shell('''./mvnw clean verify deploy''')
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
