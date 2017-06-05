package org.springframework.jenkins.cloud.e2e

import org.springframework.jenkins.cloud.common.SpringCloudJobs
import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.cloud.common.SpringCloudNotification
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Label
import org.springframework.jenkins.common.job.Slack
import org.springframework.jenkins.common.job.TestPublisher

/**
 * @author Marcin Grzejszczak
 */
class EndToEndBuildMaker implements TestPublisher,
		JdkConfig, BreweryDefaults, Label, Cron, SpringCloudJobs {

	private static final int MAX_EC2_EXECUTORS = 1

	private final DslFactory dsl
	private final String organization

	EndToEndBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = "spring-cloud"
	}

	EndToEndBuildMaker(DslFactory dsl, String organization) {
		this.dsl = dsl
		this.organization = organization
	}

	void build(String projectName, String cronExpr) {
		build(projectName, "scripts/runAcceptanceTests.sh", cronExpr)
	}

	void build(String projectName, String scriptName, String cronExpr, boolean withTests = true) {
		build(projectName, projectName, scriptName, cronExpr, withTests)
	}

	void buildWithGradleAndMavenTests(String projectName, String scriptName, String cronExpr) {
		build(projectName, projectName, scriptName, cronExpr, true, '', true)
	}

	protected void build(String projectName, String repoName, String scriptName, String cronExpr,
						 boolean withTests = true, String postBuildScripts = "", boolean mavenTests = false) {
		String organization = this.organization
		dsl.job("${prefixJob(projectName)}-e2e") {
			triggers {
				cron cronExpr
			}
			jdk jdk8()
			wrappers {
				timestamps()
				colorizeOutput()
				label aws()
				environmentVariables([
						TERM: 'dumb',
						RETRIES: 70,
						(jdk8HomeEnvVar()): jdk8DefaultPath()
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
			weight(MAX_EC2_EXECUTORS)
			steps {
				shell(killAllApps())
				shell("""
					echo "Cleaning up .m2"
					rm -rf ~/.m2/repository/org/springframework/cloud/launcher 
				""")
				shell("""
						sh -e ${scriptName}
					""")
				if (postBuildScripts) {
					shell("""
						sh -e ${postBuildScripts}
					""")
				}
			}
			configure {
				customConfiguration(projectName, it as Node)
			}
			publishers {
				if (withTests) {
					archiveJunit gradleJUnitResults()
					archiveArtifacts acceptanceTestReports()
					archiveArtifacts acceptanceTestSpockReports()
				}
				if (mavenTests) {
					archiveJunit mavenJUnitResults()
				}
			}
		}
	}

	protected void customConfiguration(String projectName, Node node) {
		Slack slack = SpringCloudNotification.cloudSlack(node)
		if (projectName.contains("stream")) {
			slack.room([SpringCloudNotification.CLOUD_ROOM, SpringCloudNotification.STREAM_ROOM].join(","))
		}
	}
}
