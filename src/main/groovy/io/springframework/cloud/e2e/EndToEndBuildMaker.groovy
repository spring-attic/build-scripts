package io.springframework.cloud.e2e

import io.springframework.cloud.common.SpringCloudJobsConfig
import io.springframework.common.*
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class EndToEndBuildMaker implements Notification, Publisher, JdkConfig, BreweryDefaults, Label, Cron, SpringCloudJobsConfig {

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
		build(projectName, "runAcceptanceTests", cronExpr)
	}

	void build(String projectName, String scriptName, String cronExpr, boolean withTests = true) {
		build(projectName, projectName, scriptName, cronExpr, withTests)
	}

	void buildWithoutTests(String projectName, String scriptName, String cronExpr) {
		build(projectName, projectName, scriptName, cronExpr, false)
	}

	protected void build(String projectName, String repoName, String scriptName, String cronExpr, boolean withTests = true) {
		String organization = this.organization
		dsl.job("${prefixJob(projectName)}-e2e") {
			triggers {
				cron cronExpr
			}
			jdk jdk8()
			wrappers {
				label aws()
				environmentVariables([
						RETRIES: 70,
						(jdk8HomeEnvVar()): jdk8DefaultPath(),
						(pathToJavaBinEnvVar()): jdk8DefaultPath()
				])
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
				shell('''
						echo "Clearing mvn and gradle repos"
						rm -rf ~/.m2/repository/org/springframework/cloud/
						rm -rf ~/.gradle/caches/modules-2/files-2.1/org.springframework.cloud/
					''')
				shell("""
						sh -e scripts/${scriptName}.sh
					""")
			}
			configure {
				appendSlackNotificationForSpringCloud(it as Node)
			}
			publishers {
				if (withTests) {
					archiveJunit gradleJUnitResults()
					archiveArtifacts acceptanceTestReports()
					archiveArtifacts acceptanceTestSpockReports()
				}
			}
		}
	}

}
