package io.springframework.springboot.ci

import io.springframework.common.*
import io.springframework.springboot.common.SpringBootJobs
import io.springframework.springboot.common.SpringBootNotification
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class SpringBootDeployBuildMaker implements SpringBootNotification, JdkConfig, TestPublisher,
		Cron, SpringBootJobs, Maven, Artifactory {
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

	void deploy(String project, boolean checkTests = true) {
		dsl.job("${prefixJob(project)}-ci") {
			triggers {
				cron everyThreeHours()
				githubPush()
			}
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/${organization}/${project}"
						branches '1.0.x', '1.1.x', '1.2.x', '1.3.x'
					}
				}
			}
			steps {
				maven {
					mavenInstallation(maven30())
					goals('install -U -P snapshot,prepare,ci -DskipTests')
				}
			}
			configure {
				slackNotificationForSpring(it as Node)
				artifactoryMavenBuild(it as Node, maven30(), '-U -P full -s settings.xml', '-Xmx2g -XX:MaxPermSize=512m')
				artifactoryMaven3Configurator(it as Node, '**/*-tests.jar,**/*-site.jar,**/*spring-boot-sample*,**/*spring-boot-integration-tests*,**/*.effective-pom,**/*-starter-poms.zip')
			}
			if (checkTests) {
				publishers {
					archiveJunit mavenJUnitResults()
				}
			}
		}
	}

	void deployWithoutTests(String project) {
		deploy(project, false)
	}
}
