package scstappstarters

import org.springframework.jenkins.scstappstarters.ci.SpringScstAppStatersPhasedBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

String releaseType = "" // possible values are - "", milestone or ga

// Master CI
new SpringScstAppStatersPhasedBuildMaker(dsl).build(true, "milestone")

// 1.3.x CI
new SpringScstAppStatersPhasedBuildMaker(dsl).build(false, "", "1.3.x")

// 2.0.x CI
new SpringScstAppStatersPhasedBuildMaker(dsl).build(false, "", "2.0.x")
