package io.springframework.cloud.e2e

import io.springframework.cloud.common.SpringCloudJobs
import io.springframework.cloud.common.SpringCloudNotification
import io.springframework.common.*
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class EndToEndBuildMaker implements SpringCloudNotification, TestPublisher,
		JdkConfig, BreweryDefaults, Label, Cron, SpringCloudJobs {

	private static final int MAX_EC2_EXECUTORS = 1

	private final DslFactory dsl
	private final String organization
	final String instanceLabel

	EndToEndBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = "spring-cloud"
		this.instanceLabel = aws()
	}

	EndToEndBuildMaker(DslFactory dsl, String organization) {
		this.dsl = dsl
		this.organization = organization
		this.instanceLabel = aws()
	}

	EndToEndBuildMaker(DslFactory dsl, String organization, String label) {
		this.dsl = dsl
		this.organization = organization
		this.instanceLabel = label
	}

	void build(String projectName, String cronExpr) {
		build(projectName, "scripts/runAcceptanceTests.sh", cronExpr)
	}

	void buildWithGradleAndMavenTests(String projectName, String cronExpr) {
		buildWithGradleAndMavenTests(projectName, "scripts/runAcceptanceTests.sh", cronExpr)
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
				if (instanceLabel) {
					label instanceLabel
				}
				environmentVariables([
						TERM: 'dumb',
						RETRIES: 70,
						(jdk8HomeEnvVar()): jdk8DefaultPath()
				])
				timeout {
					noActivity(300)
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
				SlackPlugin.slackNotification(it as Node) {
					room(cloudRoom())
				}
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

}
