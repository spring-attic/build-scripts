package io.springframework.cloud.ci

import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Spencer Gibb
 */
class SpringCloudKubernetesDeployBuildMaker extends SpringCloudBranchBuildMaker {

	SpringCloudKubernetesDeployBuildMaker(DslFactory dsl) {
		super(dsl, 'spring-cloud-incubator', 'spring-cloud-kubernetes')
	}

}
