package io.springframework.cloud.e2e

import io.springframework.cloud.common.SpringCloudJobs
import io.springframework.cloud.common.SpringCloudNotification
import io.springframework.common.job.Cron
import io.springframework.common.job.JdkConfig
import io.springframework.common.job.TestPublisher
import javaposse.jobdsl.dsl.DslFactory
/**
 * @author Marcin Grzejszczak
 */
class SpringCloudSamplesTestsBuildMaker implements TestPublisher,
		JdkConfig, BreweryDefaults, Cron, SpringCloudJobs {

	private final DslFactory dsl
	private final String organization

	SpringCloudSamplesTestsBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = "spring-cloud-samples"
	}

	SpringCloudSamplesTestsBuildMaker(DslFactory dsl, String organization) {
		this.dsl = dsl
		this.organization = organization
	}

	void buildForDalston() {
		build("Dalston.BUILD-SNAPSHOT", "tests-dalston")
	}

	void buildForEdgware() {
		build("Edgware.BUILD-SNAPSHOT", "tests-edgware")
	}

	private void build(String cloudTrainVersion, String projectName, String cronExpr = everySixHours(), String branchName = masterBranch()) {
		String organization = this.organization
		dsl.job("${prefixJob(projectName)}-${branchName}-e2e") {
			triggers {
				cron cronExpr
			}
			jdk jdk8()
			wrappers {
				timestamps()
				colorizeOutput()
				timeout {
					noActivity(defaultInactivity())
					failBuild()
					writeDescription('Build failed due to timeout after {0} minutes of inactivity')
				}
			}
			scm {
				git {
					remote {
						url "https://github.com/${organization}/tests"
						branch branchName
					}
					extensions {
						wipeOutWorkspace()
					}
				}
			}
			steps {
				shell("""
						./mvnw --fail-at-end clean package -Dspring-cloud.version=${cloudTrainVersion} -U
					""")
			}
			configure {
				SpringCloudNotification.cloudSlack(it as Node)
			}
			publishers {
				archiveJunit mavenJUnitResults()
			}
		}
	}

}
