package org.springframework.jenkins.cloud.compatibility

import org.springframework.jenkins.cloud.common.HashicorpTrait
import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.common.job.TestPublisher

import static org.springframework.jenkins.cloud.common.AllCloudConstants.LATEST_SPRING_VERSION

/**
 * @author Marcin Grzejszczak
 */
class ConsulCompatibilityBuildMaker extends CompatibilityTasks implements TestPublisher, HashicorpTrait {
	private final DslFactory dsl
	private final String suffix

	ConsulCompatibilityBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.suffix = 'compatibility-check'
	}

	ConsulCompatibilityBuildMaker(DslFactory dsl, String suffix) {
		this.dsl = dsl
		this.suffix = suffix
	}

	void buildForBoot(String cronExpr = '') {
		doBuildForBoot(cronExpr)
	}

	void buildWithoutTestsForBoot(String cronExpr = '') {
		doBuildForBoot(cronExpr, false)
	}

	void buildForSpring(String cronExpr = '') {
		doBuildForSpring(cronExpr)
	}

	void buildWithoutTestsForSpring(String cronExpr = '') {
		doBuildForSpring(cronExpr, false)
	}

	private void doBuildForBoot(String cronExpr = '', boolean tests = true) {
		String projectName = 'spring-cloud-consul'
		dsl.job("${projectName}-${suffix}") {
			concurrentBuild()
			parameters {
				stringParam(SPRING_BOOT_VERSION_VAR, DEFAULT_BOOT_VERSION, 'Which version of Spring Boot should be used for the build')
			}
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
			steps {
				steps defaultStepsForBoot()
				shell postConsulShell()
			}
			if (tests) {
				publishers {
					archiveJunit mavenJUnitResults()
				}
			}
		}
	}

	private void doBuildForSpring(String cronExpr = '', boolean tests = true) {
		String projectName = 'spring-cloud-consul'
		dsl.job("${projectName}-spring-${suffix}") {
			concurrentBuild()
			parameters {
				stringParam(SPRING_VERSION_VAR, LATEST_SPRING_VERSION, 'Which version of Spring should be used for the build')
			}
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
			steps {
				steps defaultStepsForSpring()
				shell postConsulShell()
			}
			if (tests) {
				publishers {
					archiveJunit mavenJUnitResults()
				}
			}
		}
	}

	@Override
	protected String compileProductionForBoot() {
		return "${preConsulShell()} \n ${super.compileProductionForBoot()} || ${postConsulShell()}"
	}

	@Override
	protected String compileProductionForSpring() {
		return "${preConsulShell()} \n ${super.compileProductionForSpring()} || ${postConsulShell()}"
	}
}
