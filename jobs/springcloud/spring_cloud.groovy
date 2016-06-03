package springcloud

import io.springframework.cloud.ci.*
import io.springframework.cloud.common.AllCloudJobs
import io.springframework.cloud.compatibility.BootCompatibilityBuildMaker
import io.springframework.cloud.compatibility.ClusterCompatibilityBuildMaker
import io.springframework.cloud.compatibility.CompatibilityBuildMaker
import io.springframework.cloud.compatibility.ConsulCompatibilityBuildMaker
import io.springframework.cloud.e2e.CloudFoundryEndToEndBuildMaker
import io.springframework.cloud.e2e.EndToEndBuildMaker
import io.springframework.cloud.e2e.SleuthEndToEndBuildMaker
import io.springframework.cloud.f2f.AppDeployingBuildMaker
import javaposse.jobdsl.dsl.DslFactory

import static io.springframework.cloud.compatibility.CompatibilityBuildMaker.DEFAULT_SUFFIX

DslFactory dsl = this

def allProjects = AllCloudJobs.ALL_JOBS
def allIncubatorProjects = AllCloudJobs.ALL_INCUBATOR_JOBS

def projectsWithTests = allProjects - 'spring-cloud-build'

// COMPATIBILITY BUILDS
(allProjects - ['spring-cloud-consul', 'spring-cloud-build', 'spring-cloud-cluster']).each { String projectName->
	new CompatibilityBuildMaker(dsl).build(projectName, everyThreeHours())
}
new CompatibilityBuildMaker(dsl).buildWithoutTests('spring-cloud-build', everyThreeHours())
new CompatibilityBuildMaker(dsl, DEFAULT_SUFFIX, 'spring-cloud-samples').build('tests', everyThreeHours())
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
	maker.deployWithoutTests('spring-cloud-build')
}
new ConsulSpringCloudDeployBuildMaker(dsl).deploy()
new ClusterSpringCloudDeployBuildMaker(dsl).deploy()

// CI BUILDS FOR INCUBATOR
allIncubatorProjects.each {
	//new SpringCloudIncubatorDeployBuildMaker(dsl).deploy(it)
}

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

// CUSTOM E2E
// Josh's CI APP
new EndToEndBuildMaker(dsl, 'joshlong').with {
	buildWithoutTests('bootiful-microservices',
			'scenario_brixton_tester',
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
