package springcloudgcp

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.springcloudgcp.SpringCloudGcpDeployBuildMaker

DslFactory dsl = this

// CI
new SpringCloudGcpDeployBuildMaker(dsl).deploy(false, "")