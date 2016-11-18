package springcloudtaskappstarters

import io.springframework.springcloudtaskappstarters.ci.SpringCloudTaskAppStatersPhasedBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// CI
new SpringCloudTaskAppStatersPhasedBuildMaker(dsl).build(false)