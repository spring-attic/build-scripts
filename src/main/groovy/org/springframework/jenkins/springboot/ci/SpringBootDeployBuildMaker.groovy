package org.springframework.jenkins.springboot.ci

import org.springframework.jenkins.springboot.common.SpringBootNotification
import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.SlackPlugin
import org.springframework.jenkins.common.job.TestPublisher
import org.springframework.jenkins.springboot.common.SpringBootJobs

import static org.springframework.jenkins.common.job.Artifactory.artifactoryMavenBuild
/**
 * @author Marcin Grzejszczak
 */
class SpringBootDeployBuildMaker implements SpringBootNotification, JdkConfig, TestPublisher,
		Cron, SpringBootJobs, Maven {
	private static final List<String> BRANCHES_TO_BUILD = ['master', '1.4.x', '1.5.x']

	private final DslFactory dsl
	final String organization

	SpringBootDeployBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = 'spring-projects'
	}

	SpringBootDeployBuildMaker(DslFactory dsl, String organization) {
		this.dsl = dsl
		this.organization = organization
	}

	void deploy() {
		String project = 'spring-boot'
		BRANCHES_TO_BUILD.each { String branchToBuild ->
			dsl.job("${prefixJob(project)}-$branchToBuild-ci") {
				triggers {
					githubPush()
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
				steps {
					maven {
						mavenInstallation(maven33())
						goals('clean install -U -P snapshot,prepare,ci -DskipTests')
					}
				}
				configure {
					SlackPlugin.slackNotification(it as Node) {
						room(bootRoom())
					}
					artifactoryMavenBuild(it as Node) {
						mavenVersion(maven33())
						goals('clean install -U -P full -s settings.xml')
						rootPom('spring-boot-full-build/pom.xml')
						mavenOpts('-Xmx2g -XX:MaxPermSize=512m')
					}
				}
				publishers {
					archiveJunit mavenJUnitResults()
					archiveJunit mavenJUnitFailsafeResults()
				}
			}
		}
	}
}
