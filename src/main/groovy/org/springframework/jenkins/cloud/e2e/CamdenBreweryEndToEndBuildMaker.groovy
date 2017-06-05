package org.springframework.jenkins.cloud.e2e

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.cloud.common.AllCloudConstants

/**
 * @author Marcin Grzejszczak
 */
class CamdenBreweryEndToEndBuildMaker extends EndToEndBuildMaker {
	private final String repoName = 'brewery'

	CamdenBreweryEndToEndBuildMaker(DslFactory dsl) {
		super(dsl, 'spring-cloud-samples')
	}

	void build() {
		buildWithSwitches("camden", defaultSwitches())
	}

	void buildForLatestBoot() {
		buildForBoot("camden-latest-boot", AllCloudConstants.LATEST_BOOT_VERSION)
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
		return "--killattheend -v Camden.BUILD-SNAPSHOT -r"
	}
}
