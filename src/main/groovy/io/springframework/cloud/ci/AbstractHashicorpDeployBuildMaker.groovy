package io.springframework.cloud.ci

import groovy.transform.PackageScope
import io.springframework.cloud.common.HashicorpTrait
import io.springframework.cloud.common.SpringCloudJobs
import io.springframework.common.Cron
import io.springframework.common.JdkConfig
import io.springframework.common.Notification
import io.springframework.common.Publisher
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
@PackageScope
abstract class AbstractHashicorpDeployBuildMaker implements Notification, JdkConfig, Publisher, HashicorpTrait,
		Cron, SpringCloudJobs {
	protected final DslFactory dsl
	protected final String organization
	protected final String project

	AbstractHashicorpDeployBuildMaker(DslFactory dsl, String organization, String project) {
		this.dsl = dsl
		this.organization = organization
		this.project = project
	}

	void deploy() {
		dsl.job("$project-ci") {
			triggers {
				cron everyThreeHours()
				githubPush()
			}
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/${organization}/${project}"
						branch 'master'
					}
				}
			}
			steps {
				shell(cleanup())
				shell(buildDocsWithGhPages())
				shell("""\
						${preStep()}
						${cleanAndDeploy()} || ${postStep()}
					""")
				shell postStep()
			}
			configure {
				appendSlackNotificationForSpringCloud(it as Node)
			}
			publishers {
				archiveJunit mavenJUnitResults()
			}
		}
	}

	protected abstract String preStep()
	protected abstract String postStep()
}
