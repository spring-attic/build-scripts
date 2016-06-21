package io.springframework.cloud.ci

import io.springframework.cloud.common.SpringCloudJobs
import io.springframework.common.Cron
import io.springframework.common.JdkConfig
import io.springframework.common.Notification
import io.springframework.common.Publisher
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class SpringCloudContractDeployBuildMaker implements Notification, JdkConfig, Publisher, Cron,
		SpringCloudJobs {
	private final DslFactory dsl
	final String organization

	SpringCloudContractDeployBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = 'Codearte'
	}

	SpringCloudContractDeployBuildMaker(DslFactory dsl, String organization) {
		this.dsl = dsl
		this.organization = organization
	}

	void deployMaven(String projectLabel, String projectName) {
		dsl.job("${prefixJob(projectLabel)}-ci") {
			triggers {
				cron everyThreeHours()
				githubPush()
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
			}
			steps {
				shell(cleanup())
				shell(buildDocs())
				shell(cleanAndDeploy())
			}
			configure {
				appendSlackNotificationForSpringCloud(it as Node)
			}
			publishers {
				archiveJunit mavenJUnitResults()
			}
		}
	}

	void deployGradle(String projectLabel, String projectName) {
		dsl.job("${prefixJob(projectLabel)}-ci") {
			triggers {
				scm(every15Minutes())
				githubPush()
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
				// That way we'll have the test results from
				shell('''
					echo "Installing project locally"
					./gradlew clean build install
					''')
				shell('''
					echo "Checking if local installation is working fine with samples"
					./scripts/runTests.sh
					''')
				shell("""
					echo "Uploading snapshots (since the build is working fine)"
					./gradlew uploadArchives -P${repoUserNameEnvVar()}=\$${repoUserNameEnvVar()} \
-P${repoPasswordEnvVar()}=\$${repoPasswordEnvVar()}
					""")
			}
			configure {
				appendSlackNotificationForSpringCloud(it as Node)
			}
			publishers {
				archiveJunit mavenJUnitResults()
				archiveJunit gradleJUnitResults()
			}
		}
	}
}
