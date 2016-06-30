package springboot

import javaposse.jobdsl.dsl.DslFactory

import static io.springframework.springboot.common.AllSpringBootJobs.ALL_JOBS

DslFactory dsl = this

println "Projects with tests $ALL_JOBS"

// CI
ALL_JOBS.each {
	//new SpringBootDeployBuildMaker(dsl).deploy('spring-boot')
}
