package io.springframework.common

import groovy.transform.CompileStatic

/**
 * @author Marcin Grzejszczak
 */
@CompileStatic
trait JdkConfig {

	String jdk8() {
		return "jdk8"
	}

	String jdk7() {
		return "jdk7"
	}

	String jdk8HomeEnvVar() {
		return 'JAVA_HOME'
	}

	String jdk8DefaultPath() {
		return '/opt/jdk-8'
	}
}
