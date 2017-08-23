package springio

import org.springframework.jenkins.springio.ci.SpringInitializrBuildMaker
import org.springframework.jenkins.springio.ci.SpringStarterProductionBuildMaker
import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.springio.ci.SpringStarterRollbackBuildMaker

DslFactory dsl = this
String scriptsDir = binding.variables["SCRIPTS_DIR"] ?: "${WORKSPACE}/src/main/bash/springio"

// CI
new SpringInitializrBuildMaker(dsl).build()
new SpringStarterProductionBuildMaker(dsl, scriptsDir).deploy()
new SpringStarterRollbackBuildMaker(dsl, scriptsDir).deploy()
