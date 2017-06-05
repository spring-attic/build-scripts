package org.springframework.jenkins.cloud.ci

import groovy.transform.PackageScope
import org.springframework.jenkins.cloud.common.HashicorpTrait
import org.springframework.jenkins.common.job.JdkConfig
import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.cloud.common.SpringCloudJobs
import org.springframework.jenkins.cloud.common.SpringCloudNotification
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.TestPublisher

/**
 * @author Marcin Grzejszczak
 */
@PackageScope
abstract class AbstractHashicorpDeployBuildMaker implements JdkConfig, TestPublisher, HashicorpTrait,
		Cron, SpringCloudJobs, Maven {
	protected final DslFactory dsl
	protected final String organization
	protected final String project

	AbstractHashicorpDeployBuildMaker(DslFactory dsl, String organization, String project) {
		this.dsl = dsl
		this.organization = organization
		this.project = project
	}

	void deploy(String branchName = 'master') {
		dsl.job("$project-$branchName-ci") {
			triggers {
				cron everyThreeHours()
				githubPush()
			}
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/${organization}/${project}"
						branch branchName
					}
					extensions {
						wipeOutWorkspace()
					}
				}
			}
			wrappers {
				timestamps()
				colorizeOutput()
				maskPasswords()
				credentialsBinding {
					usernamePassword(githubRepoUserNameEnvVar(),
							githubRepoPasswordEnvVar(),
							githubUserCredentialId())
				}
				environmentVariables {
					env('BRANCH', branchName)
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
				shell(buildDocsWithGhPages())
				shell("""\
						${preStep()}
						${cleanAndDeploy()} || ${postStep()}
					""")
				shell postStep()
			}
			configure {
				SpringCloudNotification.cloudSlack(it as Node)
			}
			publishers {
				archiveJunit mavenJUnitResults()
			}
		}
	}

	protected abstract String preStep()
	protected abstract String postStep()
}
