package springcloudgcp

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.springclouddataflowsamples.SpringCloudDataFlowSamplesBuildMaker

DslFactory dsl = this

// CI
new SpringCloudDataFlowSamplesBuildMaker(dsl).deploy()