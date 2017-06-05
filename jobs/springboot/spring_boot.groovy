package springboot

import org.springframework.jenkins.springboot.ci.SpringBootDeployBuildMaker
import org.springframework.jenkins.springboot.ci.SpringBootIntegrationBuildMaker
import org.springframework.jenkins.springboot.ci.SpringBootWindowsBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// CI
new SpringBootDeployBuildMaker(dsl).deploy()
new SpringBootWindowsBuildMaker(dsl).deploy()
new SpringBootIntegrationBuildMaker(dsl).deploy()
