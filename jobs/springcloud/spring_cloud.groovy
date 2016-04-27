package springcloud

import io.springframework.cloud.ci.BenchmarksBuildMaker
import io.springframework.cloud.ci.DocsAppBuildMaker
import io.springframework.cloud.compatibility.CompatibilityBuildMaker
import io.springframework.cloud.compatibility.ConsulCompatibilityBuildMaker
import io.springframework.cloud.e2e.CloudFoundryEndToEndBuildMaker
import io.springframework.cloud.e2e.EndToEndBuildMaker
import io.springframework.cloud.e2e.SleuthEndToEndBuildMaker
import io.springframework.cloud.sonar.ConsulSonarBuildMaker
import io.springframework.cloud.sonar.SonarBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// COMPATIBILITY BUILDS
['spring-cloud-sleuth', 'spring-cloud-netflix', 'spring-cloud-zookeeper'].each { String projectName->
	new CompatibilityBuildMaker(dsl).build(projectName, everyThreeHours())
}
new ConsulCompatibilityBuildMaker(dsl).build('spring-cloud-consul')

// BENCHMARK BUILDS
new BenchmarksBuildMaker(dsl).buildSleuth()

// CI BUILDS
new DocsAppBuildMaker(dsl).buildDocs(everyThreeHours())

// E2E BUILDS
['spring-cloud-netflix', 'spring-cloud-zookeeper', 'spring-cloud-consul'].each { String projectName ->
	new EndToEndBuildMaker(dsl).build(projectName, everyThreeHours())
}
def sleuthMaker = new SleuthEndToEndBuildMaker(dsl)
sleuthMaker.buildSleuth(everyThreeHours())
sleuthMaker.buildSleuthStream(everyThreeHours())

// E2E on CF
def cfMaker = new CloudFoundryEndToEndBuildMaker(dsl)
cfMaker.buildBreweryForDocs()
cfMaker.buildSleuthDocApps()
cfMaker.buildSpringCloudStream()

// SONAR BUILDS
['spring-cloud-bus', 'spring-cloud-commons', 'spring-cloud-sleuth', 'spring-cloud-netflix', 'spring-cloud-zookeeper'].each {
	new SonarBuildMaker(dsl).buildSonar(it)
}
new ConsulSonarBuildMaker(dsl).buildSonar()

// ========== FUNCTIONS ==========

String everyThreeHours() {
	return "H H/3 * * *"
}