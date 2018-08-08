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

	String gpgSecRing() {
		return 'FOO_SEC'
	}

	String gpgPubRing() {
		return 'FOO_PUB'
	}

	String gpgPassphrase() {
		return 'FOO_PASSPHRASE'
	}

	String sonatypeUser() {
		return 'SONATYPE_USER'
	}

	String sonatypePassword() {
		return 'SONATYPE_PASSWORD'
	}

}