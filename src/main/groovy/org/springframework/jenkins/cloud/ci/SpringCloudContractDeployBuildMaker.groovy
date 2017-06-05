package org.springframework.jenkins.cloud.ci

import org.springframework.jenkins.cloud.common.SpringCloudJobs
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.TestPublisher
import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.cloud.common.SpringCloudNotification

/**
 * @author Marcin Grzejszczak
 */
class SpringCloudContractDeployBuildMaker implements JdkConfig, TestPublisher, Cron,
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
				shell("rm -rf /opt/jenkins/data/tools/hudson.tasks.Maven_MavenInstallation/maven33/")
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
				SpringCloudNotification.cloudSlack(it as Node)
			}
			publishers {
				archiveJunit mavenJUnitResults()
				archiveJunit gradleJUnitResults()
			}
		}
	}
}
