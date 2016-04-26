package io.springframework.common

/**
 * @author Marcin Grzejszczak
 */
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
