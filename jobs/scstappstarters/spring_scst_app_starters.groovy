package scstappstarters

import io.springframework.scstappstarters.ci.SpringScstAppStatersPhasedBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// CI
new SpringScstAppStatersPhasedBuildMaker(dsl).build(false)