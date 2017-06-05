package org.springframework.jenkins.cloud.common

import groovy.transform.CompileStatic

/**
 * Contains lists of jobs. By default we create the jobs and views in the following way
 *
 * ${project-name}-${branch-name}-ci
 *
 * e.g.
 *
 * spring-cloud-sleuth-master-ci
 * spring-cloud-netflix-1.0.x-ci
 *
 * @author Marcin Grzejszczak
 */
@CompileStatic
class AllCloudJobs {
	/**
	 * List of all Spring Cloud jobs. This list will be used to create the boot compatibility builds
	 * and will serve as basis for the default jobs
	 */
	public static final List<String> ALL_JOBS = ['spring-cloud-sleuth', 'spring-cloud-netflix', 'spring-cloud-zookeeper', 'spring-cloud-consul',
												 'spring-cloud-bus', 'spring-cloud-commons', 'spring-cloud-security', 'spring-cloud-config',
												 'spring-cloud-cloudfoundry', 'spring-cloud-aws', 'spring-cloud-build', 'spring-cloud-release',
												 'spring-cloud-cli', 'spring-cloud-contract', 'spring-cloud-vault']
	/**
	 * Some projects need to have the test report generation skipped (since they have no tests).
	 */
	public static final List<String> JOBS_WITHOUT_TESTS = ['spring-cloud-build', 'spring-cloud-release']

	/**
	 * Projects from this list will have the jobs with report generation
	 */
	public static final List<String> ALL_JOBS_WITH_TESTS = ALL_JOBS - JOBS_WITHOUT_TESTS

	/**
	 * Apart from projects containing libraries we also do have the samples. Currently the list
	 * is not really impressive but at least we have a hook for that
	 */
	public static final List<String> ALL_SAMPLES_JOBS = ['tests']

	/**
	 * There are some projects that require custom setup / teardown. Provide the list here.
	 * That way the default CI jobs will not get generated. You can see that there are duplicates
	 * in this list and {@link AllCloudJobs#ALL_JOBS}. That's intentional cause we need the list
	 * of names of all jobs that we have in the organization. Since some jobs are custom
	 * we will have custom implementations. Check out {@link org.springframework.jenkins.cloud.compatibility.ManualBootCompatibilityBuildMaker}
	 * for more info.
	 */
	public static final List<String> CUSTOM_BUILD_JOBS = ['spring-cloud-consul', 'spring-cloud-build', 'spring-cloud-contract', 'spring-cloud-vault']

	/**
	 * {@link AllCloudJobs#ALL_DEFAULT_JOBS} creates jobs for master branch. Sometimes you need other branches.
	 * That's why it's enough to provide the name of the project and the list of branches to build
	 */
	public static final Map<String, List<String>> JOBS_WITH_BRANCHES = ['spring-cloud-sleuth' : ['1.1.x', '2.0.x'],
																		'spring-cloud-netflix' : ['1.0.x', '1.1.x', '1.2.x', '2.0.x'],
																		'spring-cloud-cli' : ['1.0.x', '1.1.x'],
																		'spring-cloud-commons' : ['1.0.x', '1.1.x', '2.0.x'],
																		'spring-cloud-config' : ['1.1.x', '1.2.x', '2.0.x'],
																		'spring-cloud-zookeeper' : ['1.0.x'],
																		'spring-cloud-bus': ['1.2.x', '2.0.x'],
																		'spring-cloud-build': ['1.2.x', '2.0.x'],
																		'spring-cloud-security': ['1.1.x']]
	/**
	 * {@link AllCloudJobs#ALL_DEFAULT_JOBS} for some jobs we don't want to check whether their branches
	 * compile properly against latest boot version. Here we provide a list of such jobs
	 */
	public static final List<String> IGNORED_PROJECT_BRANCHES_FOR_COMPATIBILITY_BUILD = ['spring-cloud-commons']

	/**
	 * List of default jobs. Default means that `./mvnw clean deploy` will be executed to publish artifacts
	 * and `./mvwn clean install -Pdocs` + `gh-pages.sh` script will be executed to publish new docs.
	 * The docs will get published only for master.
	 */
	public static final List<String> ALL_DEFAULT_JOBS = ALL_JOBS - CUSTOM_BUILD_JOBS

	/**
	 * List of jobs that don't need boot compatibility tests.
	 */
	public static final List<String> JOBS_WITHOUT_BOOT_COMPATIBILITY = ['spring-cloud-cli']

	/**
	 * List of all jobs that need to be executed when doing compatibility builds against
	 * latest version of boot. This is a list of names of jobs. The proper implementations
	 * like {@link org.springframework.jenkins.cloud.compatibility.ManualBootCompatibilityBuildMaker} or
	 * {@link org.springframework.jenkins.cloud.compatibility.BootCompatibilityBuildMaker} will try
	 * to execute the jobs having those predefined names (with a proper suffix). It's up to
	 * the implementors to ensure that those jobs really exist.
	 */
	public static final List<String> BOOT_COMPATIBILITY_BUILD_JOBS = ALL_JOBS + ALL_SAMPLES_JOBS - JOBS_WITHOUT_BOOT_COMPATIBILITY

	/**
	 * List of all jobs that need to be executed when doing compatibility builds against
	 * latest version of spring. This is a list of names of jobs. The proper implementations
	 * like {@link org.springframework.jenkins.cloud.compatibility.ManualSpringCompatibilityBuildMaker} or
	 * {@link org.springframework.jenkins.cloud.compatibility.SpringCompatibilityBuildMaker} will try
	 * to execute the jobs having those predefined names (with a proper suffix). It's up to
	 * the implementors to ensure that those jobs really exist.
	 */
	public static final List<String> SPRING_COMPATIBILITY_BUILD_JOBS = ALL_JOBS + ALL_SAMPLES_JOBS - JOBS_WITHOUT_BOOT_COMPATIBILITY

	/**
	 * List of all jobs that need to be executed when doing compatibility builds against
	 * latest version of boot. This is a list of names of jobs. These jobs include only those
	 * builds that are not custom in any way. The difference between this and the {@link AllCloudJobs#BOOT_COMPATIBILITY_BUILD_JOBS}
	 * is that this one contains only default jobs whereas the other contains all jobs
	 */
	public static final List<String> DEFAULT_BOOT_COMPATIBILITY_BUILD_JOBS = ALL_DEFAULT_JOBS - JOBS_WITHOUT_BOOT_COMPATIBILITY

	/**
	 * List of all jobs that need to be executed when doing compatibility builds against
	 * latest version of spring. This is a list of names of jobs. These jobs include only those
	 * builds that are not custom in any way. The difference between this and the {@link AllCloudJobs#SPRING_COMPATIBILITY_BUILD_JOBS}
	 * is that this one contains only default jobs whereas the other contains all jobs
	 */
	public static final List<String> DEFAULT_SPRING_COMPATIBILITY_BUILD_JOBS = ALL_DEFAULT_JOBS - JOBS_WITHOUT_BOOT_COMPATIBILITY


	/**
	 * Jobs with branches to be checked against latest boot versions. Defaults to latest branch of {@link AllCloudJobs#JOBS_WITH_BRANCHES}
	 */
	public static final Map<String, List<String>> JOBS_WITH_BRANCHES_FOR_COMPATIBILITY_BUILD = JOBS_WITH_BRANCHES.collectEntries {
		String project, List<String> branches ->
			if (IGNORED_PROJECT_BRANCHES_FOR_COMPATIBILITY_BUILD.contains(project)) {
				return [:]
			}
			return [(project) : [branches.last()]]
	} as Map<String, List<String>>
}
