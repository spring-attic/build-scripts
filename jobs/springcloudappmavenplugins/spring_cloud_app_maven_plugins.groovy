package springcloudappmavenplugins

import io.springframework.springcloudappmavenplugins.ci.SpringCloudAppMavenPluginsBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// CI
new SpringCloudAppMavenPluginsBuildMaker(dsl, "spring-cloud", "spring-cloud-app-starter-doc-maven-plugin").deploy()

