package org.springframework.jenkins.springio.ci

import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.common.job.BashCloudFoundry
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.Pipeline
import org.springframework.jenkins.common.job.SlackPlugin
import org.springframework.jenkins.common.job.TestPublisher
import org.springframework.jenkins.springio.common.AllSpringIoJobs
import org.springframework.jenkins.springio.common.SpringIoJobs
import org.springframework.jenkins.springio.common.SpringIoNotification

/**
 * @author Marcin Grzejszczak
 */
class SpringStarterRollbackBuildMaker implements SpringIoNotification, JdkConfig, TestPublisher,
		Cron, SpringIoJobs, Pipeline, Maven, BashCloudFoundry {
	private final DslFactory dsl
	private final String scriptsDir
	private final String organization
	private final String branchName

    SpringStarterRollbackBuildMaker(DslFactory dsl, String scriptsDir) {
		this.dsl = dsl
		this.organization = 'spring-io'
		this.branchName = 'master'
		this.scriptsDir = scriptsDir
	}

    SpringStarterRollbackBuildMaker(DslFactory dsl, String scriptsDir, String organization, String branchName) {
		this.dsl = dsl
		this.organization = organization
		this.branchName = branchName
		this.scriptsDir = scriptsDir
	}

	void deploy() {
		dsl.job(jobName()) {
			deliveryPipelineConfiguration('Rollback', 'Rollback to previous instance')
			wrappers {
				defaultDeliveryPipelineVersion()
				credentialsBinding {
					usernamePassword('CF_USERNAME', 'CF_PASSWORD', cfCredentialsId())
				}
				environmentVariables {
					env("ROLLBACK", "true")
				}
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
				shell("""#!/bin/bash
				set -e
				
				${dsl.readFileFromWorkspace(scriptsDir + '/blueGreen.sh')}
				""")
			}
			configure {
				SlackPlugin.slackNotification(it as Node) {
					room(springRoom())
					notifySuccess(true)
				}
			}
		}
	}

	static String jobName() {
		return "${AllSpringIoJobs.getInitializrName()}-rollback"
	}
}
