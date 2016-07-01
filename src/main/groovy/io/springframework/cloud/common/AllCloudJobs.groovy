package io.springframework.cloud.common

import groovy.transform.CompileStatic

/**
 * @author Marcin Grzejszczak
 */
@CompileStatic
class AllCloudJobs {
	public static final List<String> ALL_JOBS = ['spring-cloud-sleuth', 'spring-cloud-netflix', 'spring-cloud-zookeeper', 'spring-cloud-consul',
												 'spring-cloud-bus', 'spring-cloud-commons', 'spring-cloud-security', 'spring-cloud-config',
												 'spring-cloud-cloudfoundry', 'spring-cloud-aws', 'spring-cloud-build', 'spring-cloud-cluster',
												 'spring-cloud-starters']
	public static final List<String> JOBS_WITHOUT_TESTS = ['spring-cloud-build', 'spring-cloud-starters']
	public static final List<String> ALL_JOBS_WITH_TESTS = ALL_JOBS - JOBS_WITHOUT_TESTS
	public static final List<String> ALL_SAMPLES_JOBS = ['tests']
	public static final List<String> CUSTOM_BUILD_JOBS = ['spring-cloud-consul', 'spring-cloud-build', 'spring-cloud-cluster']
	public static final Map<String, List<String>> JOBS_WITH_BRANCHES = ['spring-cloud-sleuth' : ['1.0.x'],
																		'spring-cloud-netflix' : ['1.0.x', '1.1.x'],
																		'spring-cloud-config' : ['1.1.x']]
	public static final List<String> ALL_DEFAULT_JOBS = ALL_JOBS - CUSTOM_BUILD_JOBS
}
