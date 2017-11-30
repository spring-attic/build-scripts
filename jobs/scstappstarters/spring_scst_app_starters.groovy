package scstappstarters

import org.springframework.jenkins.scstappstarters.ci.SpringScstAppStatersPhasedBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

String releaseType = "" // possible values are - "", milestone or ga

// CI
new SpringScstAppStatersPhasedBuildMaker(dsl).build(true, releaseType)