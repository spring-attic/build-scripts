package springcommon

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.cloud.ci.SpringCloudDeployBuildMaker

DslFactory dsl = this

// CI for tooling - we're using Spring Cloud classes cause Spring Cloud team
// will take care of the main tooling
new SpringCloudDeployBuildMaker(dsl, 'spring-projects').deploy("jenkins-common", false)
