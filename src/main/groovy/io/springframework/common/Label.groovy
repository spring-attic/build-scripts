package io.springframework.common

import groovy.transform.CompileStatic

/**
 * @author Marcin Grzejszczak
 */
@CompileStatic
trait Label {
	String e2e() {
		return 'ec2-0'
	}
}