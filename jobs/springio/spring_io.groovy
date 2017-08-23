package springio

import org.springframework.jenkins.springio.ci.SpringInitializrBuildMaker
import org.springframework.jenkins.springio.ci.SpringStarterProductionBuildMaker
import javaposse.jobdsl.dsl.DslFactory

import org.springframework.jenkins.springio.ci.SpringStarterRollbackBuildMaker

DslFactory dsl = this
String scriptsDir = binding.variables["SCRIPTS_DIR"] ?: "${WORKSPACE}/src/main/bash/springio"
Map<String, String> variables = binding.variables

// CI
new SpringInitializrBuildMaker(dsl).build()
new SpringStarterProductionBuildMaker(dsl, scriptsDir, variables).deploy()
new SpringStarterRollbackBuildMaker(dsl, scriptsDir, variables).deploy()
