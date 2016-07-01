package io.springframework.springio.ci

import io.springframework.common.Cron
import io.springframework.common.JdkConfig
import io.springframework.common.Maven
import io.springframework.common.Pipeline
import io.springframework.common.TestPublisher
import io.springframework.springio.common.AllSpringIoJobs
import io.springframework.springio.common.SpringIoJobs
import io.springframework.springio.common.SpringIoNotification
import javaposse.jobdsl.dsl.DslFactory

import static io.springframework.common.Artifactory.artifactoryMaven3Configurator
import static io.springframework.common.Artifactory.artifactoryMavenBuild

/**
 * @author Marcin Grzejszczak
 */
class SpringStarterBuildMaker implements SpringIoNotification, JdkConfig, TestPublisher,
		Cron, SpringIoJobs, Maven, Pipeline {
	private final DslFactory dsl
	final String organization

	SpringStarterBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = 'spring-io'
	}

	SpringStarterBuildMaker(DslFactory dsl, String organization) {
		this.dsl = dsl
		this.organization = organization
	}

	void build() {
		dsl.job("${initializrName()}-build") {
			deliveryPipelineConfiguration('Build', 'Build')
			wrappers {
				defaultDeliveryPipelineVersion()
			}
			triggers {
				cron everyThreeHours()
				githubPush()
			}
			parameters {
				//stringParam(branchVar(), masterBranch(), 'Which branch should be built')
				stringParam(branchVar(), 'maven-migration', 'Which branch should be built')
			}
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/${organization}/initializr"
						branch "\$${branchVar()}"
					}
				}
			}
			configure {
				slackNotificationForSpring(it as Node)
				artifactoryMavenBuild(it as Node) {
					mavenVersion(maven33())
					goals('clean install')
				}
				artifactoryMaven3Configurator(it as Node)
			}
			publishers {
				archiveJunit mavenJUnitResults()
				downstreamParameterized {
					trigger(SpringStarterProductionBuildMaker.jobName())
				}
			}
		}
	}

	static String jobName() {
		return "${AllSpringIoJobs.getInitializrName()}-build"
	}
}
