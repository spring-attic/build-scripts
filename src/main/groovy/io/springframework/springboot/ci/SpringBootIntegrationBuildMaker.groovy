package io.springframework.springboot.ci

import io.springframework.common.job.Cron
import io.springframework.common.job.JdkConfig
import io.springframework.common.job.Label
import io.springframework.common.job.Maven
import io.springframework.common.job.SlackPlugin
import io.springframework.common.job.TestPublisher
import io.springframework.springboot.common.SpringBootJobs
import io.springframework.springboot.common.SpringBootNotification
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class SpringBootIntegrationBuildMaker implements SpringBootNotification, JdkConfig, TestPublisher,
		Cron, SpringBootJobs, Maven, Label {
	private static final List<String> BRANCHES_TO_BUILD = ['master', '1.4.x', '1.5.x']

	private final DslFactory dsl
	final String organization

	SpringBootIntegrationBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = 'spring-projects'
	}

	SpringBootIntegrationBuildMaker(DslFactory dsl, String organization) {
		this.dsl = dsl
		this.organization = organization
	}

	void deploy() {
		String project = 'spring-boot'
		BRANCHES_TO_BUILD.each { String branchToBuild ->
			dsl.job("${prefixJob(project)}-$branchToBuild-integration-ci") {
				triggers {
					cron(everyDatAtFullHour(12))
				}
				jdk jdk8()
				scm {
					git {
						remote {
							url "https://github.com/${organization}/${project}"
							branch branchToBuild
						}
					}
				}
				wrappers {
					environmentVariables {
						env('DOCKER_URL', 'unix:///var/run/docker.sock')
					}
				}
				steps {
					maven {
						mavenInstallation(maven33())
						goals('install -U -P snapshot,prepare,ci -DskipTests')
					}
					maven {
						rootPOM('spring-boot-integration-tests/spring-boot-launch-script-tests/pom.xml')
						mavenInstallation(maven32())
						goals('-Pdocker clean verify')
					}
				}
				configure {
					SlackPlugin.slackNotification(it as Node) {
						room(bootRoom())
					}
				}
				publishers {
					archiveJunit mavenJUnitFailsafeResults()
				}
			}
		}
	}
}
