package io.springframework.cloud.compatibility

import io.springframework.cloud.common.AllCloudJobs
import javaposse.jobdsl.dsl.DslFactory

import static io.springframework.cloud.compatibility.CompatibilityTasks.DEFAULT_BOOT_VERSION
import static io.springframework.cloud.compatibility.CompatibilityTasks.SPRING_BOOT_VERSION_VAR

/**
 * Creates the jobs for the Boot Compatibility verifier
 *
 * @author Marcin Grzejszczak
 */
class BootCompatibilityBuildMaker {
	private static final String BOOT_COMPATIBILITY_SUFFIX = 'compatibility-boot-check'

	private final DslFactory dsl

	BootCompatibilityBuildMaker(DslFactory dsl) {
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
					AllCloudJobs.ALL_JOBS.each { String projectName ->
						phaseJob("$projectName-${BOOT_COMPATIBILITY_SUFFIX}".toString()) {
							currentJobParameters()
						}
					}
				}
			}
		}
	}

	void buildAllRelatedJobs() {
		(AllCloudJobs.ALL_JOBS - ['spring-cloud-consul', 'spring-cloud-build', 'spring-cloud-cluster']).each { String projectName->
			new CompatibilityBuildMaker(dsl, BOOT_COMPATIBILITY_SUFFIX).build(projectName)
		}
		new CompatibilityBuildMaker(dsl, BOOT_COMPATIBILITY_SUFFIX).buildWithoutTests('spring-cloud-build')
		new ConsulCompatibilityBuildMaker(dsl, BOOT_COMPATIBILITY_SUFFIX).build()
		new ClusterCompatibilityBuildMaker(dsl, BOOT_COMPATIBILITY_SUFFIX).build()
	}
}
