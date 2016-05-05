package io.springframework.cloud.sonar

import io.springframework.common.Cron
import io.springframework.common.JdkConfig
import io.springframework.common.Notification
import io.springframework.common.Publisher
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.helpers.step.StepContext

/**
 * @author Marcin Grzejszczak
 */
class SonarBuildMaker implements Notification, JdkConfig, Publisher, SonarTrait, Cron {
	private  static final String ONCE_PER_DAY = "H H * * *"
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
					createTag(false)
				}
			}
			jdk jdk8()
			steps defaultSteps()
			publishers {
				archiveArtifacts mavenJunitResults()
			}
			configure {
				appendSlackNotificationForSpringCloud(it as Node)
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
				./mvnw \$SONAR_MAVEN_GOAL -Psonar -Dsonar.jdbc.driverClassName=org.postgresql.Driver -Dsonar.jdbc.url=\$SONAR_JDBC_URL -Dsonar.host.url=\$SONAR_HOST_URL -Dsonar.jdbc.username=\$SONAR_JDBC_USERNAME -Dsonar.jdbc.password=\$SONAR_JDBC_PASSWORD || ${postAction()}
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
