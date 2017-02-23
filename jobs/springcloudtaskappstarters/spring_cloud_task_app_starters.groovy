package springcloudtaskappstarters

import io.springframework.springcloudtaskappstarters.ci.SpringCloudTaskAppStatersPhasedBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// CI builds for spring cloud task app starters
new SpringCloudTaskAppStatersPhasedBuildMaker(dsl).build(true)