package scstappstarters

import io.springframework.scstappstarters.common.SpringScstAppStartersBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// CI
new SpringScstAppStartersBuildMaker(dsl).deploy()

