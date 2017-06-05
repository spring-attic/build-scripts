package org.springframework.jenkins.springio.ci

import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.springio.common.AllSpringIoJobs
import org.springframework.jenkins.springio.common.SpringIoNotification
import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.Pipeline
import org.springframework.jenkins.common.job.SlackPlugin
import org.springframework.jenkins.common.job.TestPublisher
import org.springframework.jenkins.springio.common.SpringIoJobs

import static org.springframework.jenkins.common.job.Artifactory.artifactoryMaven3Configurator
import static org.springframework.jenkins.common.job.Artifactory.artifactoryMavenBuild

/**
 * @author Marcin Grzejszczak
 */
class SpringInitializrBuildMaker implements SpringIoNotification, JdkConfig, TestPublisher,
		Cron, SpringIoJobs, Maven, Pipeline {
	private final DslFactory dsl
	private final String organization
	private final String branchName

	SpringInitializrBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = 'spring-io'
		this.branchName = 'master'
	}

	SpringInitializrBuildMaker(DslFactory dsl, String organization, String branchName) {
		this.dsl = dsl
		this.organization = organization
		this.branchName = branchName
	}

	void build() {
		dsl.job(jobName()) {
			deliveryPipelineConfiguration('Build', 'Build')
			wrappers {
				defaultDeliveryPipelineVersion()
			}
			triggers {
				githubPush()
			}
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/${organization}/initializr"
						branch branchName
					}
				}
			}
			steps {
				shell("""
						echo "Removing the stored stubs"
						rm -rf ~/.m2/repository/io/spring/initializr/initializr-web/
				""")
			}
			configure {
				SlackPlugin.slackNotification(it as Node) {
					room(springRoom())
					notifySuccess(true)
				}
				artifactoryMavenBuild(it as Node) {
					mavenVersion(maven33())
					goals('clean install -Pdocs')
				}
				artifactoryMaven3Configurator(it as Node)
			}
			publishers {
				archiveJunit mavenJUnitResults()
				downstreamParameterized {
					trigger(SpringStarterProductionBuildMaker.jobName()) {
						triggerWithNoParameters()
					}
				}
			}
		}
	}

	static String jobName() {
		return "${AllSpringIoJobs.getInitializrName()}-ci"
	}
}
