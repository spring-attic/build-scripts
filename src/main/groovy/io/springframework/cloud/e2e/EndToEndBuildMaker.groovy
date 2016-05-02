package io.springframework.cloud.e2e

import io.springframework.common.DefaultConfig
import io.springframework.common.NotificationTrait
import io.springframework.common.PublisherTrait
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class EndToEndBuildMaker implements NotificationTrait, PublisherTrait, DefaultConfig, BreweryDefatuts {

	private final DslFactory dsl

	EndToEndBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void build(String projectName, String cronExpr) {
		build(projectName, "runAcceptanceTests", cronExpr)
	}

	protected void build(String projectName, String scriptName, String cronExpr) {
		dsl.job("${projectName}-e2e") {
			triggers {
				cron cronExpr
			}
			jdk jdk8()
			wrappers {
				environmentVariables([
						RETRIES: 140
				])
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
			steps {
				shell('''
						echo "Clearing mvn and gradle repos"
						rm -rf ~/.m2/repository/org/springframework/cloud/
						rm -rf ~/.gradle/caches/modules-2/files-2.1/org.springframework.cloud/
					''')
				shell("""
						sh -e scripts/${scriptName}.sh
					""")
			}
			configure {
				appendSlackNotificationForSpringCloud(it as Node)
			}
			publishers {
				archiveJunit gradleJUnitResults()
				archiveArtifacts acceptanceTestReports()
				archiveArtifacts acceptanceTestSpockReports()
			}
		}
	}

}
