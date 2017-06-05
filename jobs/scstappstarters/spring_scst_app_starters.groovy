package scstappstarters

import org.springframework.jenkins.scstappstarters.ci.SpringScstAppStatersPhasedBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// CI
new SpringScstAppStatersPhasedBuildMaker(dsl).build(false)