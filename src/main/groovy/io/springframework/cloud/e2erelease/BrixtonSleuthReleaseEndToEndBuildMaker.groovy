package io.springframework.cloud.e2erelease

import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class BrixtonSleuthReleaseEndToEndBuildMaker extends BrixtonReleaseEndToEndBuildMaker {
	BrixtonSleuthReleaseEndToEndBuildMaker(DslFactory dsl) {
		super(dsl)
	}

	void buildSleuth(String cronExpr) {
		super.build("spring-cloud-sleuth", cronExpr)
	}

	void buildSleuthStream(String cronExpr) {
		super.build("spring-cloud-sleuth", "runAcceptanceTestsStream", cronExpr)
	}

}
