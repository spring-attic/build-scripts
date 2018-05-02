package springcloudappstartermavenplugins

import org.springframework.jenkins.springcloudappstartermavenplugins.ci.SpringCloudAppStarterStarterMavenPluginsBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// CI
new SpringCloudAppStarterStarterMavenPluginsBuildMaker(dsl, "spring-cloud", "spring-cloud-app-starters-maven-plugins", "spring-cloud-app-starter-doc-maven-plugin").deploy(false, true)
new SpringCloudAppStarterStarterMavenPluginsBuildMaker(dsl, "spring-cloud", "spring-cloud-app-starters-maven-plugins", "spring-cloud-app-starter-metadata-maven-plugin").deploy()
new SpringCloudAppStarterStarterMavenPluginsBuildMaker(dsl, "spring-cloud", "spring-cloud-app-starters-maven-plugins", "spring-cloud-stream-app-maven-plugin").deploy(false, true)

