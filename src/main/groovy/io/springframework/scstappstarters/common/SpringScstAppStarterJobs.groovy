package io.springframework.scstappstarters.common

import io.springframework.common.BuildAndDeploy

/**
 * @author Marcin Grzejszczak
 */
trait SpringScstAppStarterJobs extends BuildAndDeploy {

	@Override
	String projectSuffix() {
		return 'spring-scst-app-starters'
	}

}