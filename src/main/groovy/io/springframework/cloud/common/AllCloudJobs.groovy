package io.springframework.cloud.common

import groovy.transform.CompileStatic

/**
 * @author Marcin Grzejszczak
 */
@CompileStatic
class AllCloudJobs {
	public static final List<String> ALL_JOBS = ['spring-cloud-sleuth', 'spring-cloud-netflix', 'spring-cloud-zookeeper', 'spring-cloud-consul',
												 'spring-cloud-bus', 'spring-cloud-commons', 'spring-cloud-config', 'spring-cloud-security',
												 'spring-cloud-cloudfoundry', 'spring-cloud-aws', 'spring-cloud-build', 'spring-cloud-cluster',
												 'spring-cloud-starters']
	public static final List<String> ALL_SAMPLES_JOBS = ['tests']
}
