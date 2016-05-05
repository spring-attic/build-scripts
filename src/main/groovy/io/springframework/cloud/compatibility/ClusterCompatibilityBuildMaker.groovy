package io.springframework.cloud.compatibility

import io.springframework.cloud.common.ClusterTrait
import io.springframework.common.Publisher
import javaposse.jobdsl.dsl.DslFactory
/**
 * @author Marcin Grzejszczak
 */
class ClusterCompatibilityBuildMaker extends CompatibilityTasks implements Publisher, ClusterTrait {
	private final DslFactory dsl

	ClusterCompatibilityBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void build(String projectName, String cronExpr = "H H/3 * * *") {
		dsl.job("${projectName}-compatibility-check") {
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
			steps {
				shell preClusterShell()
				steps defaultSteps()
				shell postClusterShell()
			}
			publishers {
				archiveJunit mavenJunitResults()
			}
		}
	}

	@Override
	protected String runTests() {
		return "${super.runTests()} || ${postConsulShell()}"
	}
}
