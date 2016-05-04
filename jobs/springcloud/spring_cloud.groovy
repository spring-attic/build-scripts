package springcloud

import io.springframework.cloud.ci.BenchmarksBuildMaker
import io.springframework.cloud.ci.DocsAppBuildMaker
import io.springframework.cloud.ci.CloudDeployBuildMaker
import io.springframework.cloud.compatibility.CompatibilityBuildMaker
import io.springframework.cloud.compatibility.ConsulCompatibilityBuildMaker
import io.springframework.cloud.e2e.CloudFoundryEndToEndBuildMaker
import io.springframework.cloud.e2e.EndToEndBuildMaker
import io.springframework.cloud.e2e.SleuthEndToEndBuildMaker
import io.springframework.cloud.f2f.AppDeployingBuildMaker
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
new CloudDeployBuildMaker(dsl).with { CloudDeployBuildMaker maker ->
	['sleuth', 'netflix', 'zookeeper', 'consul', 'bus',
	 'commons', 'config', 'security', 'cloudfoundry', 'aws', 'build',
	'cluster'].each {
		maker.deploy("spring-cloud-$it")
	}
}

// E2E BUILDS
['spring-cloud-netflix', 'spring-cloud-zookeeper', 'spring-cloud-consul'].each { String projectName ->
	new EndToEndBuildMaker(dsl).build(projectName, everyThreeHours())
}
new SleuthEndToEndBuildMaker(dsl).with {
	buildSleuth(everyThreeHours())
	buildSleuthStream(everyThreeHours())
}

// E2E on CF
new CloudFoundryEndToEndBuildMaker(dsl).with {
	buildBreweryForDocs()
	buildSleuthDocApps()
	buildSpringCloudStream()
}

// SONAR BUILDS - disabled until sonar is set
/*['spring-cloud-bus', 'spring-cloud-commons', 'spring-cloud-sleuth', 'spring-cloud-netflix', 'spring-cloud-zookeeper'].each {
	new SonarBuildMaker(dsl).buildSonar(it)
}
new ConsulSonarBuildMaker(dsl).buildSonar()*/

// F2F
new AppDeployingBuildMaker(dsl).with {
	build('marcingrzejszczak', 'atom-feed')
	build('dsyer', 'github-analytics')
}

// ========== FUNCTIONS ==========

String everyThreeHours() {
	return "H H/3 * * *"
}