package springio

import io.springframework.springio.ci.SpringInitializrBuildMaker
import io.springframework.springio.ci.SpringStarterProductionBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// CI
new SpringInitializrBuildMaker(dsl).build()
new SpringStarterProductionBuildMaker(dsl).deploy()
