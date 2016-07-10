package io.springframework.cloud.ci

import io.springframework.cloud.common.SpringCloudJobs
import io.springframework.cloud.common.SpringCloudNotification
import io.springframework.common.Cron
import io.springframework.common.JdkConfig
import io.springframework.common.TestPublisher
import javaposse.jobdsl.dsl.DslFactory
/**
 * @author Marcin Grzejszczak
 */
class SpringCloudContractDeployBuildMaker implements SpringCloudNotification, JdkConfig, TestPublisher, Cron,
		SpringCloudJobs {
	private final DslFactory dsl
	final String organization
	final String projectName

	SpringCloudContractDeployBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = 'spring-cloud'
		this.projectName = 'spring-cloud-contract'
	}

	SpringCloudContractDeployBuildMaker(DslFactory dsl, String organization, String projectName = 'spring-cloud-contract') {
		this.dsl = dsl
		this.organization = organization
		this.projectName = projectName
	}

	void deploy() {
		String projectLabel = projectName
		dsl.job("${prefixJob(projectLabel)}-ci") {
			triggers {
				cron everyThreeHours()
				githubPush()
			}
			parameters {
				stringParam(branchVar(), masterBranch(), 'Which branch should be built')
			}
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/${organization}/${projectName}"
						branch 'master'
					}
				}
			}
			wrappers {
				maskPasswords()
				credentialsBinding {
					usernamePassword(repoUserNameEnvVar(), repoPasswordEnvVar(),
							repoSpringIoUserCredentialId())
				}
			}
			steps {
				shell(cleanup())
				shell(buildDocs())
				shell('''
					 echo "Running Spring Cloud Contract Verifier Core"
					./mvnw clean install
					''')
				shell('''
					echo "Running Spring Cloud Contract Verifier Gradle Plugin"
					cd spring-cloud-contract-verifier-gradle-plugin
					./gradlew clean build install
					''')
				shell('''
					 echo "Checking if samples are not broken"
					./scripts/runTests.sh
					''')
				shell('''
					echo "Uploading snapshots of Maven stuff"
					./mvnw clean deploy -DskipTests
					''')
				shell("""
					echo "Uploading snapshots of Gradle stuff"
					cd spring-cloud-contract-verifier-gradle-plugin
					set +x
					./gradlew uploadArchives -P${repoUserNameEnvVar()}=\$${repoUserNameEnvVar()} \
-P${repoPasswordEnvVar()}=\$${repoPasswordEnvVar()} -x test
					set -x
					""")
			}
			configure {
				slackNotificationForSpringCloud(it as Node)
			}
			publishers {
				archiveJunit mavenJUnitResults()
				archiveJunit gradleJUnitResults()
			}
		}
	}
}
