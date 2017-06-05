package org.springframework.jenkins.cloud.ci

import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Spencer Gibb
 */
class SpringCloudGatewayDeployBuildMaker extends SpringCloudDeployBuildMaker {

	SpringCloudGatewayDeployBuildMaker(DslFactory dsl) {
		super(dsl, 'spring-cloud-incubator')
	}

	void deploy() {
		super.deploy("spring-cloud-gateway", masterBranch())
	}
}
