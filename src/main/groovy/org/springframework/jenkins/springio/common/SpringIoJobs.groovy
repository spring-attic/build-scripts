package org.springframework.jenkins.springio.common

import org.springframework.jenkins.common.job.BuildAndDeploy

/**
 * @author Marcin Grzejszczak
 */
trait SpringIoJobs extends BuildAndDeploy {

	@Override
	String projectSuffix() {
		return 'spring-io'
	}

	String initializrName() {
		return "${projectSuffix()}-initializr"
	}
}