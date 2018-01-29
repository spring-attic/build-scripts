package scstappstarters

import org.springframework.jenkins.scstappstarters.ci.SpringScstAppStatersPhasedBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

String releaseType = "milestone" // possible values are - "", milestone or ga

// Master CI
new SpringScstAppStatersPhasedBuildMaker(dsl).build(true, releaseType)

// 1.3.x CI
new SpringScstAppStatersPhasedBuildMaker(dsl).build(false, releaseType, "1.3.x")