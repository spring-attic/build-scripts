package springboot

import io.springframework.springboot.ci.SpringBootDeployBuildMaker
import io.springframework.springboot.ci.SpringBootWindowsBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// CI
new SpringBootDeployBuildMaker(dsl).deploy()
new SpringBootWindowsBuildMaker(dsl).deploy()
