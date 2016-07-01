package springio

import io.springframework.springio.ci.SpringStarterBuildMaker
import io.springframework.springio.ci.SpringStarterProductionBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// CI
new SpringStarterBuildMaker(dsl, 'snicoll', 'maven-migration').build()
new SpringStarterProductionBuildMaker(dsl, 'snicoll', 'maven-migration').deploy()
