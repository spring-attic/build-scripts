package org.springframework.jenkins.springio.ci

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.Pipeline
import org.springframework.jenkins.common.job.SlackPlugin
import org.springframework.jenkins.common.job.TestPublisher
import org.springframework.jenkins.springio.common.AllSpringIoJobs
import org.springframework.jenkins.springio.common.SpringIoJobs
import org.springframework.jenkins.springio.common.SpringIoNotification

import static org.springframework.jenkins.common.job.CloudFoundryPlugin.pushToCloudFoundry

/**
 * @author Marcin Grzejszczak
 */
class SpringStarterProductionBuildMaker implements SpringIoNotification, JdkConfig, TestPublisher,
		Cron, SpringIoJobs, Pipeline, Maven {
	private final DslFactory dsl
	private final String organization
	private final String branchName

	SpringStarterProductionBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = 'spring-io'
		this.branchName = 'master'
	}

	SpringStarterProductionBuildMaker(DslFactory dsl, String organization, String branchName) {
		this.dsl = dsl
		this.organization = organization
		this.branchName = branchName
	}

	void deploy() {
		dsl.job(jobName()) {
			deliveryPipelineConfiguration('Deploy', 'Push to CF')
			wrappers {
				defaultDeliveryPipelineVersion()
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
				maven {
					goals('clean package')
					mavenInstallation(maven30())
					rootPOM('initializr-service/pom.xml')
				}
			}
			configure {
				SlackPlugin.slackNotification(it as Node) {
					room(springRoom())
					notifySuccess(true)
				}
				pushToCloudFoundry(it as Node) {
					organization('spring.io')
					cloudSpace('production')
					manifestConfig {
						appName('start')
						appPath('initializr-service/target/initializr-service.jar')
						domain()
					}
				}
			}
		}
	}

	static String jobName() {
		return "${AllSpringIoJobs.getInitializrName()}-production"
	}
}
