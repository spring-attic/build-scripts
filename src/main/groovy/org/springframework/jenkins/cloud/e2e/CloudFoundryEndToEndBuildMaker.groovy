package org.springframework.jenkins.cloud.e2e

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.cloud.common.SpringCloudJobs
import org.springframework.jenkins.cloud.common.SpringCloudNotification
import org.springframework.jenkins.common.job.BashCloudFoundry
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.TestPublisher

/**
 * @author Marcin Grzejszczak
 */
class CloudFoundryEndToEndBuildMaker implements TestPublisher, JdkConfig, BreweryDefaults,
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
				SpringCloudNotification.cloudSlack(it as Node)
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
