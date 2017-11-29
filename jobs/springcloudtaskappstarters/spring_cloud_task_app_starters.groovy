package springcloudtaskappstarters

import org.springframework.jenkins.springcloudtaskappstarters.ci.SpringCloudTaskAppStatersPhasedBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

String releaseType = "" // possible values are - milestone or ga

// CI builds for spring cloud task app starters
new SpringCloudTaskAppStatersPhasedBuildMaker(dsl).build(false, releaseType)