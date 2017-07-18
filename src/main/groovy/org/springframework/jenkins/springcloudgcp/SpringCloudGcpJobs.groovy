package org.springframework.jenkins.springcloudgcp

import org.springframework.jenkins.common.job.BuildAndDeploy

/**
 * @author Marcin Grzejszczak
 */
trait SpringCloudGcpJobs extends BuildAndDeploy {

	@Override
	String projectSuffix() {
		return 'spring-cloud-gcp'
	}

}