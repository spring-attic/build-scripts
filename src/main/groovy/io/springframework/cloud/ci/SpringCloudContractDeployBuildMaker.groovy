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

	void deployVerifierMavenPlugin(String projectLabel) {
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
				shell(prepareDirectoryChange(cleanup()))
				shell(prepareDirectoryChange(buildDocs()))
				shell(prepareDirectoryChange(cleanAndDeploy()))
			}
			configure {
				slackNotificationForSpringCloud(it as Node)
			}
			publishers {
				archiveJunit mavenJUnitResults()
			}
		}
	}

	private String prepareDirectoryChange(String command) {
		return "cd \$WORKSPACE/spring-cloud-contract-verifier/spring-cloud-contract-verifier-maven-plugin; $command"
	}

	void deployVerifier(String projectLabel) {
		dsl.job("${prefixJob(projectLabel)}-ci") {
			triggers {
				scm(every15Minutes())
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
						branch "\$${branchVar()}"
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
					cd $WORKSPACE/spring-cloud-contract-verifier/spring-cloud-contract-verifier
					./gradlew clean build install
					''')
				shell('''
					echo "Checking if local installation is working fine with samples"
					cd $WORKSPACE/spring-cloud-contract-verifier/spring-cloud-contract-verifier
					./scripts/runTests.sh
					''')
				shell("""
					echo "Uploading snapshots (since the build is working fine)"
					cd \$WORKSPACE/spring-cloud-contract-verifier/spring-cloud-contract-verifier
					set +x
					./gradlew uploadArchives -P${repoUserNameEnvVar()}=\$${repoUserNameEnvVar()} \
-P${repoPasswordEnvVar()}=\$${repoPasswordEnvVar()}
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
