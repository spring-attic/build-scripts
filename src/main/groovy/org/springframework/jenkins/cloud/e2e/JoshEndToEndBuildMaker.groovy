package org.springframework.jenkins.cloud.e2e

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.cloud.common.SpringCloudJobs
import org.springframework.jenkins.cloud.common.SpringCloudNotification
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Label
import org.springframework.jenkins.common.job.TestPublisher

/**
 * @author Marcin Grzejszczak
 */
class JoshEndToEndBuildMaker implements TestPublisher,
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
				SpringCloudNotification.cloudSlack(it as Node)
			}
		}
	}

}
