package io.springframework.springboot.ci

import io.springframework.common.job.Cron
import io.springframework.common.job.JdkConfig
import io.springframework.common.job.Maven
import io.springframework.common.job.SlackPlugin
import io.springframework.common.job.TestPublisher
import io.springframework.springboot.common.SpringBootJobs
import io.springframework.springboot.common.SpringBootNotification
import javaposse.jobdsl.dsl.DslFactory

import static io.springframework.common.job.Artifactory.artifactoryMavenBuild
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
						goals('install -U -P snapshot,prepare,ci -DskipTests')
					}
				}
				configure {
					SlackPlugin.slackNotification(it as Node) {
						room(bootRoom())
					}
					artifactoryMavenBuild(it as Node) {
						mavenVersion(maven32())
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
