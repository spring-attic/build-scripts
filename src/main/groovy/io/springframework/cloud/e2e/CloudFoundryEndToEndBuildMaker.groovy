package io.springframework.cloud.e2e

import io.springframework.common.CfConfig
import io.springframework.common.DefaultConfig
import io.springframework.common.NotificationTrait
import io.springframework.common.PublisherTrait
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class CloudFoundryEndToEndBuildMaker implements NotificationTrait, PublisherTrait, DefaultConfig, BreweryDefatuts, CfConfig {
	private static final String EVERY_DAY_AT_18 = "0 0 18 1/1 * ? *"
	private static final String EVERY_SUN_AT_14 = "0 0 14 ? * SUN *"
	private static final String EVERY_SUN_AT_16 = "0 0 16 ? * SUN *"

	private final DslFactory dsl

	CloudFoundryEndToEndBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void buildSpringCloudStream() {
		build('spring-cloud-sleuth','spring-cloud', 'spring-cloud-sleuth', "scripts/runAcceptanceTestsStreamOnCF.sh", EVERY_DAY_AT_18)
	}

	void buildBreweryForDocs() {
		build('spring-cloud-brewery-for-docs', 'spring-cloud-samples', 'brewery', "runAcceptanceTests.sh -t SLEUTH_STREAM -c -p docsbrewing", EVERY_SUN_AT_16)
	}

	void buildSleuthDocApps() {
		build('spring-cloud-sleuth-doc-apps', 'spring-cloud-samples', 'sleuth-documentation-apps', "runAcceptanceTests.sh", EVERY_SUN_AT_14)
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
					createTag(false)
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
						. ./${script}
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
