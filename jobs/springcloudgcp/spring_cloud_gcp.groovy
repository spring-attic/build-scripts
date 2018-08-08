package springcloudgcp

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.springcloudgcp.SpringCloudGcpDeployBuildMaker

DslFactory dsl = this

// CI for master
new SpringCloudGcpDeployBuildMaker(dsl).deploy(false, "", "master")

// CI for 1.0.x
new SpringCloudGcpDeployBuildMaker(dsl).deploy(true, "", "1.0.x")