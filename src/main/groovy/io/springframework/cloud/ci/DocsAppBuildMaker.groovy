package io.springframework.cloud.ci

import io.springframework.common.NotificationTrait
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class DocsAppBuildMaker implements NotificationTrait {
	private final DslFactory dsl

	DocsAppBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void buildDocs(String cronExpr = "0 0 3 1/1 * ? *") {
		dsl.job('spring-cloud-sleuth-docs-apps-ci') {
			triggers {
				cron cronExpr
			}
			scm {
				git {
					remote {
						url "https://github.com/spring-cloud-samples/sleuth-documentation-apps"
					}
					createTag(false)
				}
			}
			steps {
				gradle('clean build --parallel')
			}

			configure {
				appendSlackNotificationForSpringCloud(it as Node)
			}
		}
	}
}
