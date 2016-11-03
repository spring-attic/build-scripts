package io.springframework.cloud.e2e

import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class CamdenBreweryEndToEndBuildMaker extends EndToEndBuildMaker {
	private final String repoName = 'brewery'

	CamdenBreweryEndToEndBuildMaker(DslFactory dsl) {
		super(dsl, 'spring-cloud-samples')
	}

	void build() {
		String prefix = 'camden'
		String defaultSwitches = "--killattheend -v Camden.BUILD-SNAPSHOT -r"
		super.build("$prefix-zookeeper", repoName, "runAcceptanceTests.sh -t ZOOKEEPER $defaultSwitches", everyThreeHours())
		super.build("$prefix-sleuth", repoName, "runAcceptanceTests.sh -t SLEUTH $defaultSwitches", everyThreeHours())
		super.build("$prefix-sleuth-stream", repoName, "runAcceptanceTests.sh -t SLEUTH_STREAM $defaultSwitches", everyThreeHours())
		super.build("$prefix-sleuth-stream-kafka", repoName, "runAcceptanceTests.sh -t SLEUTH_STREAM -k $defaultSwitches", everyThreeHours())
		super.build("$prefix-eureka", repoName, "runAcceptanceTests.sh -t EUREKA $defaultSwitches", everyThreeHours())
		super.build("$prefix-consul", repoName, "runAcceptanceTests.sh -t CONSUL $defaultSwitches", everyThreeHours())
	}
}
