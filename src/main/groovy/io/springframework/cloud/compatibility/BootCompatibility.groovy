package io.springframework.cloud.compatibility

import io.springframework.cloud.common.AllCloudJobs
import javaposse.jobdsl.dsl.DslFactory

import static io.springframework.cloud.compatibility.CompatibilityTasks.DEFAULT_BOOT_VERSION
import static io.springframework.cloud.compatibility.CompatibilityTasks.SPRING_BOOT_VERSION_VAR
/**
 * @author Marcin Grzejszczak
 */
class BootCompatibility {
	private final DslFactory dsl

	BootCompatibility(DslFactory dsl) {
		this.dsl = dsl
	}

	void build() {
		dsl.multiJob('spring-cloud-boot-compatibility-check') {
			parameters {
				stringParam(SPRING_BOOT_VERSION_VAR, DEFAULT_BOOT_VERSION, 'Which version of Spring Boot should be used for the build')
			}
			steps {
				phase('spring-boot-compatibility-phase') {
					AllCloudJobs.ALL_JOBS.each { String projectName ->
						phaseJob("$projectName-compatibility-check".toString()) {
							currentJobParameters()
						}
					}
				}
			}
		}
	}
}
