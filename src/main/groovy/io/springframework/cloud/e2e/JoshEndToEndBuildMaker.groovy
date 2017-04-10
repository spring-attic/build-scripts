package io.springframework.cloud.e2e

import io.springframework.cloud.common.SpringCloudJobs
import io.springframework.cloud.common.SpringCloudNotification
import io.springframework.common.job.Cron
import io.springframework.common.job.JdkConfig
import io.springframework.common.job.Label
import io.springframework.common.job.SlackPlugin
import io.springframework.common.job.TestPublisher
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class JoshEndToEndBuildMaker implements SpringCloudNotification, TestPublisher,
		JdkConfig, BreweryDefaults, Label, Cron, SpringCloudJobs {

	private final DslFactory dsl
	private final String organization
	private final String repoName

	JoshEndToEndBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = "joshlong"
		this.repoName = 'bootiful-microservices'
	}

	void build(String projectName, String scriptName, String cronExpr, String postBuildScripts) {
		dsl.job("${prefixJob(projectName)}-e2e") {
			triggers {
				cron cronExpr
			}
			jdk jdk8()
			wrappers {
				timestamps()
				colorizeOutput()
				environmentVariables([
						RETRIES: 50
				])
				timeout {
					noActivity(defaultInactivity())
					failBuild()
					writeDescription('Build failed due to timeout after {0} minutes of inactivity')
				}
			}
			scm {
				git {
					remote {
						url "https://github.com/${organization}/$repoName"
						branch 'master'
					}
					extensions {
						wipeOutWorkspace()
					}
				}
			}
			steps {
				shell("""
						./${scriptName} && echo "Tests passed!" || (echo "Tests failed!! Clearing up" && ./${postBuildScripts} && exit 1)
					""")
				shell("""
					echo "Clearing up after successful tests"
					./${postBuildScripts}
				""")
			}
			configure {
				SlackPlugin.slackNotification(it as Node) {
					room(cloudRoom())
				}
			}
		}
	}

}
