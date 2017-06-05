package org.springframework.jenkins.cloud.compatibility

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.cloud.common.AllCloudConstants
import org.springframework.jenkins.cloud.common.SpringCloudNotification

/**
 * @author Marcin Grzejszczak
 */
class SpringCompatibilityBuildMaker extends CompatibilityBuildMaker {

	public static final String COMPATIBILITY_BUILD_SPRING_SUFFIX = 'compatibility-spring-check'

	SpringCompatibilityBuildMaker(DslFactory dsl) {
		super(dsl, COMPATIBILITY_BUILD_SPRING_SUFFIX)
	}

	SpringCompatibilityBuildMaker(DslFactory dsl, String suffix) {
		super(dsl, suffix)
	}

	SpringCompatibilityBuildMaker(DslFactory dsl, String suffix, String organization) {
		super(dsl, suffix, organization)
	}

	protected void buildWithTests(String projectName, String repoName, String branchName, String cronExpr, boolean checkTests) {
		String prefixedProjectName = prefixJob(projectName)
		dsl.job("${prefixedProjectName}-${suffix}") {
			concurrentBuild()
			parameters {
				stringParam(SPRING_VERSION_VAR, AllCloudConstants.LATEST_SPRING_VERSION, 'Which version of Spring should be used for the build')
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
			steps checkTests ? defaultStepsWithTestsForSpring() : defaultStepsForSpring()
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
