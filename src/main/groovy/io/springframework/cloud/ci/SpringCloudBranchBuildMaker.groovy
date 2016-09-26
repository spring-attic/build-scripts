package io.springframework.cloud.ci

import io.springframework.cloud.common.SpringCloudJobs
import io.springframework.cloud.common.SpringCloudNotification
import io.springframework.common.*
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class SpringCloudBranchBuildMaker implements SpringCloudNotification, JdkConfig, TestPublisher, Cron,
		SpringCloudJobs, Maven {
	private final DslFactory dsl
	final String organization

	SpringCloudBranchBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = 'spring-cloud'
	}

	SpringCloudBranchBuildMaker(DslFactory dsl, String organization) {
		this.dsl = dsl
		this.organization = organization
	}
	void deploy(String project, boolean checkTests = true) {
		dsl.job("$project-branch-ci") {
			parameters {
				stringParam(branchVar(), masterBranch(), 'Which branch should be built')
			}
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/${organization}/${project}"
						branch "\$${branchVar()}"
					}
					extensions {
						wipeOutWorkspace()
					}
				}
			}
			wrappers {
				maskPasswords()
				credentialsBinding {
					usernamePassword(githubRepoUserNameEnvVar(),
							githubRepoPasswordEnvVar(),
							githubUserCredentialId())
				}
			}
			steps {
				shell(cleanAndDeploy())
			}
			configure {
				SlackPlugin.slackNotification(it as Node) {
					room(cloudRoom())
				}
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
