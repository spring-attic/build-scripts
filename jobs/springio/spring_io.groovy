package springio

import io.springframework.springio.ci.SpringStarterDeployBuildMaker
import javaposse.jobdsl.dsl.DslFactory

import static io.springframework.springio.common.AllSpringIoJobs.ALL_JOBS

DslFactory dsl = this

println "Projects with tests $ALL_JOBS"

// CI
ALL_JOBS.each {
	new SpringStarterDeployBuildMaker(dsl).deploy(it)
}
