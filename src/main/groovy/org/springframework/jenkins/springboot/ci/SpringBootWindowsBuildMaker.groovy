package org.springframework.jenkins.springboot.ci

import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Label
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.SlackPlugin
import org.springframework.jenkins.common.job.TestPublisher
import org.springframework.jenkins.springboot.common.SpringBootJobs
import org.springframework.jenkins.springboot.common.SpringBootNotification
import javaposse.jobdsl.dsl.DslFactory
/**
 * @author Marcin Grzejszczak
 */
class SpringBootWindowsBuildMaker implements SpringBootNotification, JdkConfig, TestPublisher,
		Cron, SpringBootJobs, Maven, Label {
	private static final List<String> BRANCHES_TO_BUILD = ['master', '1.4.x', '1.5.x']

	private final DslFactory dsl
	final String organization

	SpringBootWindowsBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = 'spring-projects'
	}

	SpringBootWindowsBuildMaker(DslFactory dsl, String organization) {
		this.dsl = dsl
		this.organization = organization
	}

	void deploy() {
		String project = 'spring-boot'
		BRANCHES_TO_BUILD.each { String branchToBuild ->
			dsl.job("${prefixJob(project)}-$branchToBuild-windows-ci") {
				triggers {
					cron(everyDatAtFullHour(8))
				}
				label(windows())
				jdk jdk8()
				scm {
					git {
						remote {
							url "https://github.com/${organization}/${project}"
							branch branchToBuild
						}
					}
				}
				steps {
					maven {
						mavenInstallation(maven33())
						goals('clean install -U')
					}
				}
				configure {
					SlackPlugin.slackNotification(it as Node) {
						room(bootRoom())
					}
				}
				publishers {
					archiveJunit mavenJUnitResults()
				}
			}
		}
	}
}
