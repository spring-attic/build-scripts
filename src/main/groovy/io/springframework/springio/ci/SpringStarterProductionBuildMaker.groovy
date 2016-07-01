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

import static io.springframework.common.CloudFoundryPlugin.pushToCloudFoundry
/**
 * @author Marcin Grzejszczak
 */
class SpringStarterProductionBuildMaker implements SpringIoNotification, JdkConfig, TestPublisher,
		Cron, SpringIoJobs, Pipeline, Maven {
	private final DslFactory dsl
	final String organization

	SpringStarterProductionBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = 'spring-io'
	}

	SpringStarterProductionBuildMaker(DslFactory dsl, String organization) {
		this.dsl = dsl
		this.organization = organization
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
						branch "maven-migration"
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
				slackNotificationForSpring(it as Node)
				pushToCloudFoundry(it as Node) {
					organization('spring.io')
					cloudSpace('development')
					manifestConfig {
						appName('start')
						hostName('start-development')
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
