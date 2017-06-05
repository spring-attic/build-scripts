package org.springframework.jenkins.cloud.ci

import org.springframework.jenkins.common.job.JdkConfig
import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.cloud.common.SpringCloudJobs
import org.springframework.jenkins.cloud.common.SpringCloudNotification

/**
 * @author Marcin Grzejszczak
 */
class DocsAppBuildMaker implements JdkConfig, SpringCloudJobs {
	private final DslFactory dsl

	DocsAppBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void buildDocs(String cronExpr) {
		dsl.job('spring-cloud-sleuth-docs-apps-ci') {
			triggers {
				cron cronExpr
			}
			parameters {
				stringParam(branchVarName(), masterBranch(), 'Which branch should be built')
			}
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/spring-cloud-samples/sleuth-documentation-apps"
						branch "\$${branchVarName()}"
					}

				}
			}
			wrappers {
				timestamps()
				colorizeOutput()
				timeout {
					noActivity(300)
					failBuild()
					writeDescription('Build failed due to timeout after {0} minutes of inactivity')
				}
			}
			steps {
				gradle('clean')
				gradle('build --parallel --refresh-dependencies')
			}

			configure {
				SpringCloudNotification.cloudSlack(it as Node)
			}
		}
	}
}
