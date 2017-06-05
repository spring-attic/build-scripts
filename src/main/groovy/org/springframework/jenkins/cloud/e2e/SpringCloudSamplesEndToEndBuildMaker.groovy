package org.springframework.jenkins.cloud.e2e

import org.springframework.jenkins.cloud.common.SpringCloudJobs
import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.cloud.common.SpringCloudNotification
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Label
import org.springframework.jenkins.common.job.TestPublisher

/**
 * @author Marcin Grzejszczak
 */
class SpringCloudSamplesEndToEndBuildMaker implements TestPublisher,
		JdkConfig, BreweryDefaults, Label, Cron, SpringCloudJobs {

	private static final int MAX_EC2_EXECUTORS = 1

	private final DslFactory dsl
	private final String organization

	SpringCloudSamplesEndToEndBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = "spring-cloud-samples"
	}

	SpringCloudSamplesEndToEndBuildMaker(DslFactory dsl, String organization) {
		this.dsl = dsl
		this.organization = organization
	}

	void build(String projectName, String cronExpr) {
		build(projectName, projectName, "scripts/runAcceptanceTests.sh", cronExpr)
	}

	void buildWithoutTests(String projectName, String cronExpr) {
		build(projectName, projectName, "scripts/runAcceptanceTests.sh", cronExpr, masterBranch(), "", false, false)
	}

	void buildWithGradleAndMavenTests(String projectName, String cronExpr, String branch = masterBranch()) {
		build(projectName, projectName, "scripts/runAcceptanceTests.sh", cronExpr, branch, '', true, true)
	}

	protected void build(String projectName, String repoName, String scriptName, String cronExpr, String branchName = masterBranch(),
						 String postBuildScripts = "", boolean mavenTests = false,
						 boolean gradleTests = false) {
		String organization = this.organization
		dsl.job("${prefixJob(projectName)}-${branchName}-e2e") {
			triggers {
				cron cronExpr
			}
			jdk jdk8()
			wrappers {
				timestamps()
				colorizeOutput()
				environmentVariables([
						TERM: 'dumb',
						RETRIES: 70
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
						branch branchName
					}
					extensions {
						wipeOutWorkspace()
					}
				}
			}
			steps {
				shell("""
						./${scriptName}
					""")
				if (postBuildScripts) {
					shell("""
						./${postBuildScripts}
					""")
				}
			}
			configure {
				SpringCloudNotification.cloudSlack(it as Node)
			}
			publishers {
				if (gradleTests) {
					archiveJunit gradleJUnitResults()
				}
				if (mavenTests) {
					archiveJunit mavenJUnitResults()
				}
			}
		}
	}

}
