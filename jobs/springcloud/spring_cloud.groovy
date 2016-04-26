package springcloud

import io.springframework.cloud.ci.BenchmarksBuildMaker
import io.springframework.cloud.ci.DocsAppBuildMaker
import io.springframework.cloud.compatibility.CompatibilityBuildMaker
import io.springframework.cloud.compatibility.ConsulCompatibilityBuildMaker
import io.springframework.cloud.e2e.EndToEndBuildMaker
import io.springframework.cloud.e2e.SleuthEndToEndBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// COMPATIBILITY BUILDS

// Jobs
['spring-cloud-sleuth', 'spring-cloud-netflix', 'spring-cloud-zookeeper'].eachWithIndex { String projectName, Integer index ->
	new CompatibilityBuildMaker(dsl).build(projectName, everyDayAt(5, index))
}
new ConsulCompatibilityBuildMaker(dsl).build('spring-cloud-consul')

// BENCHMARK BUILDS
new BenchmarksBuildMaker(dsl).buildSleuth()

// CI BUILDS
new DocsAppBuildMaker(dsl).buildDocs()

// E2E BUILDS

['spring-cloud-netflix', 'spring-cloud-zookeeper', 'spring-cloud-consul'].eachWithIndex { String projectName, Integer index ->
	new EndToEndBuildMaker(dsl).build(projectName, everyDayAt(index, 10))
}
def sleuthMaker = new SleuthEndToEndBuildMaker(dsl)
sleuthMaker.buildSleuth(everyDayAt(0, 15))
sleuthMaker.buildSleuthStream(everyDayAt(1, 15))

// SONAR BUILDS

// Functions
String everyDayAt(int startingHour, int offset) {
	return "0 0 ${startingHour + offset} 1/1 * ? *"
}