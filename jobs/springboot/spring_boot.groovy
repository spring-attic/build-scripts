package springboot

import io.springframework.springboot.ci.SpringBootDeployBuildMaker
import javaposse.jobdsl.dsl.DslFactory

import static io.springframework.springboot.common.AllSpringBootJobs.ALL_JOBS

DslFactory dsl = this

println "Projects with tests $ALL_JOBS"

// CI
new SpringBootDeployBuildMaker(dsl).deploy('spring-boot')
