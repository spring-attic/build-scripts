package springio

import org.springframework.jenkins.springio.ci.SpringInitializrBuildMaker
import org.springframework.jenkins.springio.ci.SpringStarterProductionBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// CI
new SpringInitializrBuildMaker(dsl).build()
new SpringStarterProductionBuildMaker(dsl).deploy()
