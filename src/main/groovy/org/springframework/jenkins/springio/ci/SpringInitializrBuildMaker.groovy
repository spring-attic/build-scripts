package org.springframework.jenkins.springio.ci

import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.springio.common.AllSpringIoJobs
import org.springframework.jenkins.springio.common.SpringIoNotification
import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.Pipeline
import org.springframework.jenkins.common.job.SlackPlugin
import org.springframework.jenkins.common.job.TestPublisher
import org.springframework.jenkins.springio.common.SpringIoJobs

import static org.springframework.jenkins.common.job.Artifactory.artifactoryMaven3Configurator
import static org.springframework.jenkins.common.job.Artifactory.artifactoryMavenBuild

/**
 * @author Marcin Grzejszczak
 */
class SpringInitializrBuildMaker implements SpringIoNotification, JdkConfig, TestPublisher,
		Cron, SpringIoJobs, Maven, Pipeline {
	private final DslFactory dsl
	private final String organization
	private final String branchName

	SpringInitializrBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = 'spring-io'
		this.branchName = 'master'
	}

	SpringInitializrBuildMaker(DslFactory dsl, String organization, String branchName) {
		this.dsl = dsl
		this.organization = organization
		this.branchName = branchName
	}

	void build() {
		dsl.job(jobName()) {
			deliveryPipelineConfiguration('Build', 'Build')
			wrappers {
				defaultDeliveryPipelineVersion()
				parameters {
					stringParam('BLUE_APP_NAME', 'start-blue', 'The name of the blue instance')
					stringParam('GREEN_APP_NAME', 'start-green', 'The name of the green instance')
					stringParam('ROUTED_HOSTNAME', 'start-staging', 'The hostname to which the "production" traffic gets routed')
					stringParam('DOMAIN_NAME', 'cfapps.io', 'Domain of the deployed application')
					stringParam('JAR_LOCATION', 'initializr-service/target/initializr-service.jar', 'Location of the JAR to be deployed')
					stringParam('OLD_APP_INSTANCES', '1', 'Number of instances of the old instance. If you pass [0] then the old instance will get stopped')
					stringParam('NEW_APP_INSTANCES', '2', ' Number of instances of the new instance')
					stringParam('OLD_APP_MEMORY', '', 'Memory to be used by the old instance')
					stringParam('NEW_APP_MEMORY', '', 'Memory to be used by the new instance')
					stringParam('CF_ORG', 'spring.io', 'Cloud Foundry organization to which you would like to deploy the application')
					stringParam('CF_SPACE', 'staging', 'Cloud Foundry space to which you would like to deploy the application')
					stringParam('CF_API', 'api.run.pivotal.io', 'Cloud Foundry API of the installation to which you would like to deploy the application')
				}
			}
			triggers {
				githubPush()
			}
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/${organization}/initializr"
						branch branchName
					}
				}
			}
			steps {
				shell("""
						echo "Removing the stored stubs"
						rm -rf ~/.m2/repository/io/spring/initializr/initializr-web/
				""")
			}
			configure {
				SlackPlugin.slackNotification(it as Node) {
					room(springRoom())
					notifySuccess(true)
				}
				artifactoryMavenBuild(it as Node) {
					mavenVersion(maven33())
					goals('clean install -Pdocs')
				}
				artifactoryMaven3Configurator(it as Node)
			}
			steps {
				shell('''
						echo "Store parameters as a file"
						mkdir -p target
						rm -rf target/params.rc
						cat <<EOT >> target/params.rc
export BLUE_APP_NAME="${BLUE_APP_NAME:-start-blue}"
export BLUE_APP_ROUTE="${BLUE_APP_ROUTE:-${BLUE_APP_NAME:-start-blue}}"
export GREEN_APP_NAME="${GREEN_APP_NAME:-start-green}"
export ROUTED_HOSTNAME="${ROUTED_HOSTNAME:-start-staging}"
export DOMAIN_NAME="${DOMAIN_NAME:-cfapps.io}"
export JAR_LOCATION="${JAR_LOCATION:-initializr-service/target/initializr-service.jar}"
export OLD_APP_INSTANCES=${OLD_APP_INSTANCES:-1}
export NEW_APP_INSTANCES=${NEW_APP_INSTANCES:-2}
export OLD_APP_MEMORY=${OLD_APP_MEMORY:-}
export NEW_APP_MEMORY=${NEW_APP_MEMORY:-}
export CF_ORG=${CF_ORG:-}
export CF_SPACE=${CF_SPACE:-}
export CF_API=${CF_API:-api.run.pivotal.io}
export ROLLBACK=${ROLLBACK:-false}
EOT
				''')
			}
			publishers {
				archiveJunit mavenJUnitResults()
				buildPipelineTrigger([SpringStarterProductionBuildMaker.jobName(),
									  SpringStarterRollbackBuildMaker.jobName()].join(",")) {
					parameters {
						sameNode()
						setFailTriggerOnMissing(false)
					}
				}
			}
		}
	}

	static String jobName() {
		return "${AllSpringIoJobs.getInitializrName()}-ci"
	}
}
