package io.springframework.common

import groovy.transform.CompileStatic

/**
 * Contains default Jenkins variables for Cloud Foundry
 *
 * @author Marcin Grzejszczak
 */
@CompileStatic
trait CfConfig {

	String cfUsername() {
		return '$CF_USERNAME'
	}

	String cfPassword() {
		return '$CF_PASSWORD'
	}

	String cfSpace() {
		return '$CF_SPACE'
	}
}
