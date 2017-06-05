package org.springframework.jenkins.springboot.common

import org.springframework.jenkins.common.job.BuildAndDeploy

/**
 * @author Marcin Grzejszczak
 */
trait SpringBootJobs extends BuildAndDeploy {

	@Override
	String projectSuffix() {
		return 'spring-boot'
	}

}