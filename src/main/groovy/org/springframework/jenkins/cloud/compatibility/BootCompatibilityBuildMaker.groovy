package org.springframework.jenkins.cloud.compatibility

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.cloud.common.SpringCloudNotification

/**
 * @author Marcin Grzejszczak
 */
class BootCompatibilityBuildMaker extends CompatibilityBuildMaker {

	BootCompatibilityBuildMaker(DslFactory dsl) {
		super(dsl)
	}

	BootCompatibilityBuildMaker(DslFactory dsl, String suffix) {
		super(dsl, suffix)
	}

	BootCompatibilityBuildMaker(DslFactory dsl, String suffix, String organization) {
		super(dsl, suffix, organization)
	}

	protected void buildWithTests(String projectName, String repoName, String branchName, String cronExpr, boolean checkTests) {
		String prefixedProjectName = prefixJob(projectName)
		dsl.job("${prefixedProjectName}-${suffix}") {
			concurrentBuild()
			parameters {
				stringParam(SPRING_BOOT_VERSION_VAR, DEFAULT_BOOT_VERSION, 'Which version of Spring Boot should be used for the build')
			}
			triggers {
				if (cronExpr) {
					cron cronExpr
				}
			}
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/${organization}/$repoName"
						branch branchName
					}
				}
			}
			steps checkTests ? defaultStepsWithTestsForBoot() : defaultStepsForBoot()
			configure {
				SpringCloudNotification.cloudSlack(it as Node)
			}
			if (checkTests) {
				publishers {
					archiveJunit mavenJUnitResults()
				}
			}
		}
	}

}
