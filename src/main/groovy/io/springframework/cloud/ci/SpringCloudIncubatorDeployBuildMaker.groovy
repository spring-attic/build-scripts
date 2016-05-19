package io.springframework.cloud.ci

import groovy.transform.CompileStatic
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
@CompileStatic
class SpringCloudIncubatorDeployBuildMaker extends SpringCloudDeployBuildMaker {
	SpringCloudIncubatorDeployBuildMaker(DslFactory dsl) {
		super(dsl, 'spring-cloud-incubator')
	}
}
