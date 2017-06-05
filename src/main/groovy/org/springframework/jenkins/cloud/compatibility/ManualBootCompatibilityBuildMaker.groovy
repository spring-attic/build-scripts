package org.springframework.jenkins.cloud.compatibility

import org.springframework.jenkins.cloud.common.SpringCloudJobs
import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.cloud.common.AllCloudJobs

import static CompatibilityTasks.DEFAULT_BOOT_VERSION
import static CompatibilityTasks.SPRING_BOOT_VERSION_VAR
/**
 * Creates the jobs for the Boot Compatibility verifier
 *
 * @author Marcin Grzejszczak
 */
class ManualBootCompatibilityBuildMaker implements SpringCloudJobs {
	public static final String BOOT_COMPATIBILITY_SUFFIX = 'compatibility-boot-check'

	private final DslFactory dsl

	ManualBootCompatibilityBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void build() {
		buildAllRelatedJobs()
		dsl.multiJob("spring-cloud-${BOOT_COMPATIBILITY_SUFFIX}") {
			parameters {
				stringParam(SPRING_BOOT_VERSION_VAR, DEFAULT_BOOT_VERSION, 'Which version of Spring Boot should be used for the build')
			}
			steps {
				phase('spring-boot-compatibility-phase') {
					(AllCloudJobs.BOOT_COMPATIBILITY_BUILD_JOBS).each { String projectName ->
						String prefixedProjectName = prefixJob(projectName)
						phaseJob("${prefixedProjectName}-${BOOT_COMPATIBILITY_SUFFIX}".toString()) {
							currentJobParameters()
						}
					}
				}
			}
		}
	}

	void buildAllRelatedJobs() {
		AllCloudJobs.ALL_DEFAULT_JOBS.each { String projectName->
			new BootCompatibilityBuildMaker(dsl, BOOT_COMPATIBILITY_SUFFIX).buildWithoutTests(projectName)
		}
		AllCloudJobs.JOBS_WITHOUT_TESTS.each {
			new BootCompatibilityBuildMaker(dsl, BOOT_COMPATIBILITY_SUFFIX).buildWithoutTests(it)
		}
		new BootCompatibilityBuildMaker(dsl, BOOT_COMPATIBILITY_SUFFIX).buildWithoutTests("spring-cloud-contract")
		new ConsulCompatibilityBuildMaker(dsl, BOOT_COMPATIBILITY_SUFFIX).buildWithoutTestsForBoot()
		new BootCompatibilityBuildMaker(dsl, BOOT_COMPATIBILITY_SUFFIX, 'spring-cloud-samples').buildWithoutTests('tests')
	}
}
