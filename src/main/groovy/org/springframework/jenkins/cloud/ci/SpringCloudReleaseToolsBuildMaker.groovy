package org.springframework.jenkins.cloud.ci

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
class SpringCloudReleaseToolsBuildMaker implements JdkConfig, TestPublisher, Cron,
		SpringCloudJobs, Maven {
	private final DslFactory dsl
	final String organization
	final String project = "spring-cloud-release-tools"

	SpringCloudReleaseToolsBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = 'spring-cloud'
	}

	void deploy(boolean checkTests = true) {
		deploy(masterBranch(), checkTests)
	}

	void deploy(String branchToBuild, boolean checkTests = true) {
		String projectNameWithBranch = branchToBuild ? "$branchToBuild-" : ''
		dsl.job("$project-${projectNameWithBranch}ci") {
			triggers {
				githubPush()
			}
			parameters {
				stringParam(branchVarName(), branchToBuild ?: masterBranch(), 'Which branch should be built')
			}
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/${organization}/${project}"
						branch "\$${branchVarName()}"
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
				shell(buildDocsWithGhPages())
				shell(cleanAndDeploy())
			}
			configure {
				SpringCloudNotification.cloudSlack(it as Node)
			}
			if (checkTests) {
				publishers {
					archiveJunit mavenJUnitResults()
				}
			}
		}
	}
}
