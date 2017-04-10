package io.springframework.cloud.ci

import io.springframework.cloud.common.SpringCloudJobs
import io.springframework.cloud.common.SpringCloudNotification
import io.springframework.common.job.Cron
import io.springframework.common.job.JdkConfig
import io.springframework.common.job.Maven
import io.springframework.common.job.SlackPlugin
import io.springframework.common.job.TestPublisher
import javaposse.jobdsl.dsl.DslFactory
/**
 * @author Marcin Grzejszczak
 */
class SpringCloudContractDeployBuildMaker implements SpringCloudNotification, JdkConfig, TestPublisher, Cron,
		SpringCloudJobs, Maven {
	private final DslFactory dsl
	final String organization
	final String projectName

	SpringCloudContractDeployBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = 'spring-cloud'
		this.projectName = 'spring-cloud-contract'
	}

	SpringCloudContractDeployBuildMaker(DslFactory dsl, String organization, String projectName = 'spring-cloud-contract') {
		this.dsl = dsl
		this.organization = organization
		this.projectName = projectName
	}

	void deploy() {
		doDeploy("${prefixJob(projectName)}-${masterBranch()}-ci", this.projectName, masterBranch())
	}

	void deploy(String branchName) {
		doDeploy("${prefixJob(projectName)}-${branchName}-ci", this.projectName, branchName)
	}

	void branch() {
		doDeploy("${prefixJob(projectName)}-branch-ci", this.projectName, masterBranch(), false)
	}

	private void doDeploy(String projectName, String repoName, String branchName, boolean trigger = true) {
		dsl.job(projectName) {
			if (trigger) {
				triggers {
					cron everyThreeHours()
					githubPush()
				}
			}
			parameters {
				stringParam(branchVarName(), branchName, 'Which branch should be built')
			}
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/${organization}/${repoName}"
						branch branchVar()
					}
				}
			}
			wrappers {
				timestamps()
				colorizeOutput()
				maskPasswords()
				credentialsBinding {
					usernamePassword(repoUserNameEnvVar(),
							repoPasswordEnvVar(),
							repoSpringIoUserCredentialId())
					usernamePassword(githubRepoUserNameEnvVar(),
							githubRepoPasswordEnvVar(),
							githubUserCredentialId())
				}
				timeout {
					noActivity(300)
					failBuild()
					writeDescription('Build failed due to timeout after {0} minutes of inactivity')
				}
			}
			steps {
				maven {
					mavenInstallation(maven33())
					goals('--version')
				}
				shell(removeStubAndDeploy())
				shell("""#!/bin/bash -x
					export MAVEN_PATH=${mavenBin()}
					${setupGitCredentials()}
					echo "Building Spring Cloud Contract docs"
					./scripts/generateDocs.sh
					${deployDocs()}
					${cleanGitCredentials()}
					""")
			}
			configure {
				SlackPlugin.slackNotification(it as Node) {
					room(cloudRoom())
				}
			}
			publishers {
				archiveJunit mavenJUnitResults()
				archiveJunit gradleJUnitResults()
			}
		}
	}
}
