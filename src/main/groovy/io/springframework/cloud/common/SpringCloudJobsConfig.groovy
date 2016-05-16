package io.springframework.cloud.common

/**
 * @author Marcin Grzejszczak
 */
trait SpringCloudJobsConfig {

	String prefixJob(String projectName) {
		return projectName.startsWith('spring-cloud') ? projectName : "spring-cloud-${projectName}"
	}

}