package io.springframework.cloud.ci

import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class ConsulSpringCloudDeployBuildMaker extends AbstractHashicorpDeployBuildMaker {

	ConsulSpringCloudDeployBuildMaker(DslFactory dsl) {
		super(dsl, 'spring-cloud', 'spring-cloud-consul')
	}

	@Override
	protected String preStep() {
		return preConsulShell()
	}

	@Override
	protected String postStep() {
		return postConsulShell()
	}
}
