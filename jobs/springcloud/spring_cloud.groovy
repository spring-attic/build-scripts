package springcloud

import io.springframework.cloud.ci.*
import io.springframework.cloud.common.AllCloudJobs
import io.springframework.cloud.compatibility.BootCompatibilityBuildMaker
import io.springframework.cloud.compatibility.ClusterCompatibilityBuildMaker
import io.springframework.cloud.compatibility.CompatibilityBuildMaker
import io.springframework.cloud.compatibility.ConsulCompatibilityBuildMaker
import io.springframework.cloud.e2e.CloudFoundryBreweryTestExecutor
import io.springframework.cloud.e2e.CloudFoundryEndToEndBuildMaker
import io.springframework.cloud.e2e.EndToEndBuildMaker
import io.springframework.cloud.e2e.SleuthEndToEndBuildMaker
import io.springframework.cloud.f2f.AppDeployingBuildMaker
import javaposse.jobdsl.dsl.DslFactory

import static io.springframework.cloud.compatibility.CompatibilityBuildMaker.COMPATIBILITY_BUILD_DEFAULT_SUFFIX

DslFactory dsl = this

def allProjects = AllCloudJobs.ALL_JOBS
def projectsWithoutTests = ['spring-cloud-build', 'spring-cloud-starters']
def projectsWithTests = allProjects - projectsWithoutTests

// COMPATIBILITY BUILDS
(projectsWithTests - ['spring-cloud-consul', 'spring-cloud-cluster']).each { String projectName->
	new CompatibilityBuildMaker(dsl).build(projectName, everyThreeHours())
}
projectsWithoutTests.each {
	new CompatibilityBuildMaker(dsl).buildWithoutTests(it, everyThreeHours())
}
new CompatibilityBuildMaker(dsl, COMPATIBILITY_BUILD_DEFAULT_SUFFIX, 'spring-cloud-samples')
		.build('tests', everyThreeHours())
new ConsulCompatibilityBuildMaker(dsl).build(everyThreeHours())
new ClusterCompatibilityBuildMaker(dsl).build(everyThreeHours())
new BootCompatibilityBuildMaker(dsl).build()

// BENCHMARK BUILDS
new BenchmarksBuildMaker(dsl).buildSleuth()

// CI BUILDS
new DocsAppBuildMaker(dsl).buildDocs(everyThreeHours())
new SpringCloudDeployBuildMaker(dsl).with { SpringCloudDeployBuildMaker maker ->
	(projectsWithTests - ['spring-cloud-consul', 'spring-cloud-cluster']).each {
		maker.deploy(it)
	}
	projectsWithoutTests.each {
		maker.deployWithoutTests(it)
	}
}
new ConsulSpringCloudDeployBuildMaker(dsl).deploy()
new ClusterSpringCloudDeployBuildMaker(dsl).deploy()
// CI BUILDS FOR INCUBATOR
new VaultSpringCloudDeployBuildMaker(dsl).deploy()


// E2E BUILDS
['spring-cloud-netflix', 'spring-cloud-zookeeper', 'spring-cloud-consul'].eachWithIndex { String projectName, int index ->
	def maker = new EndToEndBuildMaker(dsl)
	maker.build(projectName, maker.everySixHoursStartingFrom(index + 1))
}
new SleuthEndToEndBuildMaker(dsl).with {
	buildSleuth(everySixHoursStartingFrom(4))
	buildSleuthStream(everySixHoursStartingFrom(5))
	buildSleuthStreamKafka(everySixHoursStartingFrom(6))
}

// E2E on CF
new CloudFoundryEndToEndBuildMaker(dsl).with {
	buildBreweryForDocs()
	buildSleuthDocApps()
	buildSpringCloudStream()
}
new CloudFoundryBreweryTestExecutor(dsl).buildBreweryForDocsTests()

// CUSTOM E2E
// Josh's CI APP
new EndToEndBuildMaker(dsl, 'joshlong').with {
	buildWithoutTests('bootiful-microservices',
			'scripts/scenario_brixton_tester.sh',
			everySaturday(),
			'scripts/kill_all.sh')
}
new EndToEndBuildMaker(dsl, 'hecklerm').with {
	buildWithoutTests('mark-hecklers-services',
			'DemoCIProjectSuite',
			'exerciseEndpoints.sh',
			everySaturday(),
			'scripts/kill_all.sh')
}

/*// SONAR BUILDS - disabled until sonar is set
['spring-cloud-bus', 'spring-cloud-commons', 'spring-cloud-sleuth', 'spring-cloud-netflix', 'spring-cloud-zookeeper'].each {
	new SonarBuildMaker(dsl).buildSonar(it)
}
new ConsulSonarBuildMaker(dsl).buildSonar()
new ClusterSonarBuildMaker(dsl).buildSonar()*/

// F2F
new AppDeployingBuildMaker(dsl).with {
	build('marcingrzejszczak', 'atom-feed')
	build('dsyer', 'github-analytics')
}

// ========== FUNCTIONS ==========

String everyThreeHours() {
	return "H H/3 * * *"
}
