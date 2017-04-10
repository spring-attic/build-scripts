package io.springframework.cloud.e2e

import io.springframework.cloud.common.SpringCloudJobs
import io.springframework.cloud.common.SpringCloudNotification
import io.springframework.common.job.BashCloudFoundry
import io.springframework.common.job.Cron
import io.springframework.common.job.JdkConfig
import io.springframework.common.job.SlackPlugin
import io.springframework.common.job.TestPublisher
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class CloudFoundryEndToEndBuildMaker implements SpringCloudNotification, TestPublisher, JdkConfig, BreweryDefaults,
		BashCloudFoundry, Cron, SpringCloudJobs {

	private final DslFactory dsl

	CloudFoundryEndToEndBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void buildSpringCloudStream() {
		build('spring-cloud-sleuth','spring-cloud', 'spring-cloud-sleuth', "scripts/runAcceptanceTestsStreamOnCF.sh", oncePerDay())
	}

	void buildBreweryForDocs() {
		build('spring-cloud-brewery-for-docs', 'spring-cloud-samples', 'brewery', "runAcceptanceTests.sh --whattotest SLEUTH_STREAM --usecloudfoundry --cloudfoundryprefix docsbrewing", everySunday())
	}

	void buildSleuthDocApps() {
		build('spring-cloud-sleuth-doc-apps', 'spring-cloud-samples', 'sleuth-documentation-apps', "runAcceptanceTests.sh", everySunday())
	}

	protected void build(String description, String githubOrg, String projectName, String script, String cronExpr) {
		dsl.job("${description}-on-cf-e2e") {
			triggers {
				cron cronExpr
			}
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/$githubOrg/$projectName"
						branch 'master'
					}
				}
			}
			wrappers {
				timestamps()
				colorizeOutput()
				maskPasswords()
				timeout {
					noActivity(defaultInactivity())
					failBuild()
					writeDescription('Build failed due to timeout after {0} minutes of inactivity')
				}
				credentialsBinding {
					usernamePassword(cfUsername(), cfPassword(), cfCredentialsId())
				}
				environmentVariables {
					env(cfSpacePropName(), cfSpace())
				}
			}
			steps {
				shell(cfScriptToExecute(script))
			}
			configure {
				SlackPlugin.slackNotification(it as Node) {
					room(cloudRoom())
				}
			}
			publishers {
				archiveJunit gradleJUnitResults()
				archiveArtifacts acceptanceTestReports()
				archiveArtifacts {
					pattern acceptanceTestSpockReports()
					allowEmpty()
				}
			}
		}
	}

}
