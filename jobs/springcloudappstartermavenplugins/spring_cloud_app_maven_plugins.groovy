package springcloudappstartermavenplugins

import io.springframework.springcloudappstartermavenplugins.ci.SpringCloudAppStarterStarterMavenPluginsBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// CI
new SpringCloudAppStarterStarterMavenPluginsBuildMaker(dsl, "spring-cloud", "spring-cloud-app-starter-doc-maven-plugin").deploy()
new SpringCloudAppStarterStarterMavenPluginsBuildMaker(dsl, "spring-cloud", "spring-cloud-stream-app-maven-plugin").deploy()

