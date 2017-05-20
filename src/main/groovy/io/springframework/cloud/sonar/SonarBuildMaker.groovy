package io.springframework.cloud.sonar

import io.springframework.cloud.common.SpringCloudNotification
import io.springframework.common.job.*
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.helpers.step.StepContext
/**
 * @author Marcin Grzejszczak
 */
class SonarBuildMaker implements JdkConfig, TestPublisher, SonarTrait, Cron {

	private final DslFactory dsl

	SonarBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void buildSonar(String projectName) {
		buildSonar(projectName, oncePerDay())
	}

	void buildSonar(String projectName, String cronExpr) {
		dsl.job("$projectName-sonar") {
			triggers {
				cron cronExpr
			}
			scm {
				git {
					remote {
						url "https://github.com/spring-cloud/$projectName"
						branch 'master'
					}

				}
			}
			jdk jdk8()
			steps defaultSteps()
			publishers {
				archiveArtifacts mavenJUnitResults()
			}
			configure {
				SpringCloudNotification.cloudSlack(it as Node)
				appendSonar(it as Node)
			}
		}
	}

	Closure defaultSteps() {
		return buildStep {
			shell('./mvnw clean org.jacoco:jacoco-maven-plugin:prepare-agent install -Psonar -U')
			shell("""\
				echo "Running sonar please wait..."
				set +x
				./mvnw \$SONAR_MAVEN_GOAL -Psonar -Dsonar.host.url=\$SONAR_HOST_URL -Dsonar.login=\$SONAR_AUTH_TOKEN || ${postAction()}
				set -x
				""")
		}
	}

	protected String postAction() {
		return 'echo "Tests failed"'
	}

	protected Closure buildStep(@DelegatesTo(StepContext) Closure buildSteps) {
		return buildSteps
	}
}
