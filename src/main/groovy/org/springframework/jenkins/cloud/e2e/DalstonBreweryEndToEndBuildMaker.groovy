package org.springframework.jenkins.cloud.e2e

import org.springframework.jenkins.cloud.common.AllCloudConstants
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class DalstonBreweryEndToEndBuildMaker extends EndToEndBuildMaker {
	private final String repoName = 'brewery'
	private static final String RELEASE_TRAIN_NAME = "dalston"

	DalstonBreweryEndToEndBuildMaker(DslFactory dsl) {
		super(dsl, 'spring-cloud-samples')
	}

	void build() {
		buildWithSwitches(RELEASE_TRAIN_NAME, defaultSwitches())
	}

	void buildForLatestBoot() {
		buildForBoot("${RELEASE_TRAIN_NAME}-latest-boot", AllCloudConstants.LATEST_BOOT_VERSION)
	}

	private void buildWithSwitches(String prefix, String defaultSwitches) {
		super.build("$prefix-zookeeper", repoName, "runAcceptanceTests.sh -t ZOOKEEPER $defaultSwitches", everyThreeHours())
		super.build("$prefix-sleuth", repoName, "runAcceptanceTests.sh -t SLEUTH $defaultSwitches", everyThreeHours())
		super.build("$prefix-sleuth-stream", repoName, "runAcceptanceTests.sh -t SLEUTH_STREAM $defaultSwitches", everyThreeHours())
		super.build("$prefix-sleuth-stream-kafka", repoName, "runAcceptanceTests.sh -t SLEUTH_STREAM -k $defaultSwitches", everyThreeHours())
		super.build("$prefix-eureka", repoName, "runAcceptanceTests.sh -t EUREKA $defaultSwitches", everyThreeHours())
		super.build("$prefix-consul", repoName, "runAcceptanceTests.sh -t CONSUL $defaultSwitches", everyThreeHours())
	}

	private void buildForBoot(String prefix, String bootVersion) {
		buildWithSwitches(prefix, "${defaultSwitches()} -b ${bootVersion}")
	}

	private String defaultSwitches() {
		String releaseTrain = RELEASE_TRAIN_NAME.capitalize()
		return "--killattheend -v ${releaseTrain}.BUILD-SNAPSHOT -r"
	}
}
