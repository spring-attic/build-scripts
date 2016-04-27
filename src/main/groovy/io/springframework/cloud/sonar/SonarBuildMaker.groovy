package io.springframework.cloud.sonar

import io.springframework.common.DefaultConfig
import io.springframework.common.NotificationTrait
import io.springframework.common.PublisherTrait
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.helpers.step.StepContext

/**
 * @author Marcin Grzejszczak
 */
class SonarBuildMaker implements NotificationTrait, DefaultConfig, PublisherTrait, SonarTrait {
	private  static final String ONCE_PER_DAY = "H H * * *"
	private final DslFactory dsl

	SonarBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void buildSonar(String projectName, String cronExpr = ONCE_PER_DAY) {
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
			shell('''\
				./mvnw $SONAR_MAVEN_GOAL -Psonar -Dsonar.jdbc.driverClassName=org.postgresql.Driver -Dsonar.jdbc.url=$SONAR_JDBC_URL -Dsonar.host.url=$SONAR_HOST_URL -Dsonar.jdbc.username=$SONAR_JDBC_USERNAME -Dsonar.jdbc.password=$SONAR_JDBC_PASSWORD
				''')
		}
	}

	protected Closure buildStep(@DelegatesTo(StepContext) Closure buildSteps) {
		return buildSteps
	}
}
