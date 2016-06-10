package io.springframework.cloud.ci

import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class ValutSpringCloudDeployBuildMaker extends AbstractHashicorpDeployBuildMaker {

	ValutSpringCloudDeployBuildMaker(DslFactory dsl, String organization) {
		super(dsl, organization, 'spring-cloud-vault-config')
	}

	ValutSpringCloudDeployBuildMaker(DslFactory dsl) {
		super(dsl, 'spring-cloud-incubator', 'spring-cloud-vault-config')
	}

	@Override
	protected String preStep() {
		return preVaultShell()
	}

	@Override
	protected String postStep() {
		return postVaultShell()
	}
}
