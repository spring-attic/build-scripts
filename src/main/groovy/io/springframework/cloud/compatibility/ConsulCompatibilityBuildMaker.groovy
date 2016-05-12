package io.springframework.cloud.compatibility

import io.springframework.cloud.common.ConsulTrait
import io.springframework.common.Publisher
import javaposse.jobdsl.dsl.DslFactory
/**
 * @author Marcin Grzejszczak
 */
class ConsulCompatibilityBuildMaker extends CompatibilityTasks implements Publisher, ConsulTrait {
	private final DslFactory dsl

	ConsulCompatibilityBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void build() {
		String projectName = 'spring-cloud-consul'
		String cronExpr = "H H/3 * * *"
		dsl.job("${projectName}-compatibility-check") {
			concurrentBuild()
			triggers {
				cron cronExpr
				parameters {
					stringParam(SPRING_BOOT_VERSION_VAR, DEFAULT_BOOT_VERSION, 'Which version of Spring Boot should be used for the build')
				}
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
				steps defaultSteps()
				shell postConsulShell()
			}
			publishers {
				archiveJunit mavenJunitResults()
			}
		}
	}

	@Override
	protected String runTests() {
		return "${preConsulShell()} \n ${super.runTests()} || ${postConsulShell()}"
	}
}
