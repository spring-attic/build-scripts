package org.springframework.jenkins.cloud.ci

import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Spencer Gibb
 */
class SpringCloudKubernetesDeployBuildMaker extends SpringCloudDeployBuildMaker {

	SpringCloudKubernetesDeployBuildMaker(DslFactory dsl) {
		super(dsl, 'spring-cloud-incubator')
	}

	void deploy() {
		super.deploy("spring-cloud-kubernetes", masterBranch())
	}
}
