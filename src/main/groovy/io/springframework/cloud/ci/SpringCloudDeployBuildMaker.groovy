package io.springframework.cloud.ci

import io.springframework.cloud.common.SpringCloudJobs
import io.springframework.common.Cron
import io.springframework.common.JdkConfig
import io.springframework.common.Notification
import io.springframework.common.Publisher
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class SpringCloudDeployBuildMaker implements Notification, JdkConfig, Publisher, Cron, SpringCloudJobs {
	private final DslFactory dsl
	final String organization

	SpringCloudDeployBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = 'spring-cloud'
	}

	SpringCloudDeployBuildMaker(DslFactory dsl, String organization) {
		this.dsl = dsl
		this.organization = organization
	}

	void deploy(String project, boolean checkTests = true) {
		dsl.job("$project-ci") {
			triggers {
				cron everyThreeHours()
				githubPush()
			}
			parameters {
				stringParam(branchVar(), masterBranch(), 'Which branch should be built')
			}
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/${organization}/${project}"
						branch "\$${branchVar()}"
					}
				}
			}
			steps {
				shell(cleanup())
				shell(buildDocsWithGhPages())
				shell(cleanAndDeploy())
			}
			configure {
				appendSlackNotificationForSpringCloud(it as Node)
			}
			if (checkTests) {
				publishers {
					archiveJunit mavenJUnitResults()
				}
			}
		}
	}

	void deployWithoutTests(String project) {
		deploy(project, false)
	}
}
