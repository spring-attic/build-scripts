package org.springframework.jenkins.springboot.ci

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Label
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.SlackPlugin
import org.springframework.jenkins.common.job.TestPublisher
import org.springframework.jenkins.springboot.common.SpringBootJobs
import org.springframework.jenkins.springboot.common.SpringBootNotification

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
