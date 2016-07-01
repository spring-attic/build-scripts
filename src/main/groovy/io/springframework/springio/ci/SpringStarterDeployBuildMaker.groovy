package io.springframework.springio.ci

import io.springframework.common.Cron
import io.springframework.common.JdkConfig
import io.springframework.common.Maven
import io.springframework.common.TestPublisher
import io.springframework.springio.common.SpringIoJobs
import io.springframework.springio.common.SpringIoNotification
import javaposse.jobdsl.dsl.DslFactory

import static io.springframework.common.Artifactory.artifactoryMaven3Configurator
import static io.springframework.common.Artifactory.artifactoryMavenBuild
/**
 * @author Marcin Grzejszczak
 */
class SpringStarterDeployBuildMaker implements SpringIoNotification, JdkConfig, TestPublisher,
		Cron, SpringIoJobs, Maven {
	private final DslFactory dsl
	final String organization

	SpringStarterDeployBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = 'spring-io'
	}

	SpringStarterDeployBuildMaker(DslFactory dsl, String organization) {
		this.dsl = dsl
		this.organization = organization
	}

	void deploy(String project, boolean checkTests = true) {
		dsl.job("${prefixJob(project)}-ci") {
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
						//branch "\$${branchVar()}"
						branch "maven-migration"
					}
				}
			}
			configure {
				slackNotificationForSpring(it as Node)
				artifactoryMavenBuild(it as Node) {
					mavenVersion = maven33()
					goals = 'clean install'
				}
				artifactoryMaven3Configurator(it as Node)
			}
			if (checkTests) {
				publishers {
					archiveJunit mavenJUnitResults()
				}
			}
		}
	}
}
