package io.springframework.cloud.e2e

import io.springframework.common.CfConfig
import io.springframework.common.Cron
import io.springframework.common.JdkConfig
import io.springframework.common.Notification
import io.springframework.common.Publisher
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class CloudFoundryEndToEndBuildMaker implements Notification, Publisher, JdkConfig, BreweryDefaults, CfConfig, Cron {

	private final DslFactory dsl

	CloudFoundryEndToEndBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void buildSpringCloudStream() {
		build('spring-cloud-sleuth','spring-cloud', 'spring-cloud-sleuth', "scripts/runAcceptanceTestsStreamOnCF.sh", oncePerDay())
	}

	void buildBreweryForDocs() {
		build('spring-cloud-brewery-for-docs', 'spring-cloud-samples', 'brewery', "runAcceptanceTests.sh -t SLEUTH_STREAM -c -p docsbrewing", everySunday())
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
				maskPasswords()
			}
			steps {
				shell('''
						echo "Clearing mvn and gradle repos"
						rm -rf ~/.m2/repository/org/springframework/cloud/
						rm -rf ~/.gradle/caches/modules-2/files-2.1/org.springframework.cloud/
					''')
				shell("""
						echo "Downloading Cloud Foundry"
						curl -L "https://cli.run.pivotal.io/stable?release=linux64-binary&source=github" | tar -zx

						echo "Setting alias to cf"
						alias cf=`pwd`/cf
						export cf=`pwd`/cf

						echo "Cloud foundry version"
						cf --version

						echo "Logging in to CF"
						cf api --skip-ssl-validation api.run.pivotal.io
						cf login -u ${cfUsername()} -p ${cfPassword()} -o FrameworksAndRuntimes -s ${cfSpace()}

						echo "Running script CF"
						bash ${script}
					""")
			}
			configure {
				appendSlackNotificationForSpringCloud(it as Node)
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
