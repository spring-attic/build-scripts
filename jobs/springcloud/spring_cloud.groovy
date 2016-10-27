package springcloud

import io.springframework.cloud.ci.*
import io.springframework.cloud.compatibility.BootCompatibilityBuildMaker
import io.springframework.cloud.compatibility.ClusterCompatibilityBuildMaker
import io.springframework.cloud.compatibility.CompatibilityBuildMaker
import io.springframework.cloud.compatibility.ConsulCompatibilityBuildMaker
import io.springframework.cloud.e2e.*
import io.springframework.cloud.f2f.AppDeployingBuildMaker
import io.springframework.cloud.sonar.ClusterSonarBuildMaker
import io.springframework.cloud.sonar.ConsulSonarBuildMaker
import io.springframework.cloud.sonar.SonarBuildMaker
import javaposse.jobdsl.dsl.DslFactory

import static io.springframework.cloud.common.AllCloudJobs.*
import static io.springframework.cloud.compatibility.CompatibilityBuildMaker.COMPATIBILITY_BUILD_DEFAULT_SUFFIX

DslFactory dsl = this

println "Projects with tests $ALL_JOBS_WITH_TESTS"
println "Projects without tests $JOBS_WITHOUT_TESTS"

// COMPATIBILITY BUILDS
(ALL_DEFAULT_JOBS).each { String projectName->
	new CompatibilityBuildMaker(dsl).build(projectName, everyThreeHours())
}
JOBS_WITHOUT_TESTS.each {
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
// Branch build maker that allows you to build and deploy a branch - this will be done on demand
SpringCloudBranchBuildMaker branchBuildMaker = new SpringCloudBranchBuildMaker(dsl)
new SpringCloudDeployBuildMaker(dsl).with { SpringCloudDeployBuildMaker maker ->
	(ALL_DEFAULT_JOBS).each {
		maker.deploy(it)
		branchBuildMaker.deploy(it)
	}
	JOBS_WITHOUT_TESTS.each {
		maker.deployWithoutTests(it)
		branchBuildMaker.deployWithoutTests(it)
	}
}
new SpringCloudPipelinesDeployBuildMaker(dsl).deploy()

// BRANCHES BUILD - spring-cloud organization
// Build that allows you to deploy, and build gh-pages of multiple branches. Used for projects
// where we support multiple versions
def branchMaker = new SpringCloudDeployBuildMaker(dsl)
JOBS_WITH_BRANCHES.each { String project, List<String> branches ->
	branches.each { String branch ->
		branchMaker.deploy(project, branch)
	}
}
// Brixton branch for Spring Cloud Release
branchMaker.deploy('spring-cloud-release', 'Brixton', false)

new ConsulSpringCloudDeployBuildMaker(dsl).deploy()
new ClusterSpringCloudDeployBuildMaker(dsl).deploy()
// CI BUILDS FOR INCUBATOR
new VaultSpringCloudDeployBuildMaker(dsl).deploy()
// CI BUILD FOR SPRING CLOUD CONTRACTS
new SpringCloudContractDeployBuildMaker(dsl).with {
	deploy()
}

// E2E BUILDS
new NetflixEndToEndBuildMaker(dsl).with {
	build(everySixHours())
}
['spring-cloud-zookeeper', 'spring-cloud-consul'].each { String projectName ->
	def maker = new EndToEndBuildMaker(dsl)
	maker.build(projectName, maker.everySixHours())
}
new SleuthEndToEndBuildMaker(dsl).with {
	buildSleuth(everySixHours())
	buildSleuthStream(everySixHours())
	buildSleuthStreamKafka(everySixHours())
}
// All jobs for Brixton bom e2e with Brewery
new BrixtonBreweryEndToEndBuildMaker(dsl).build()

// E2E on CF
new CloudFoundryEndToEndBuildMaker(dsl).with {
	buildBreweryForDocs()
	buildSleuthDocApps()
	buildSpringCloudStream()
}
new CloudFoundryBreweryTestExecutor(dsl).buildBreweryForDocsTests()

// CUSTOM E2E
// Josh's CI APP
new JoshEndToEndBuildMaker(dsl).with {
	build('bootiful-microservices-brixton',
			'scripts/scenario_brixton_tester.sh',
			everyThreeHours(),
			'scripts/kill_all.sh')
	build('bootiful-microservices-camden',
			'scripts/scenario_camden_tester.sh',
			everyThreeHours(),
			'scripts/kill_all.sh')
}
/*
new EndToEndBuildMaker(dsl, 'hecklerm').with {
	buildWithoutTests('mark-hecklers-services',
			'DemoCIProjectSuite',
			'exerciseEndpoints.sh',
			everySaturday(),
			'scripts/kill_all.sh')
}
*/

['spring-cloud-bus', 'spring-cloud-commons', 'spring-cloud-sleuth', 'spring-cloud-netflix',
 'spring-cloud-zookeeper', 'spring-cloud-contract'].each {
	new SonarBuildMaker(dsl).buildSonar(it)
}
new ConsulSonarBuildMaker(dsl).buildSonar()
new ClusterSonarBuildMaker(dsl).buildSonar()

// F2F
new AppDeployingBuildMaker(dsl).with {
	build('marcingrzejszczak', 'atom-feed')
	build('dsyer', 'github-analytics')
}

// ========== FUNCTIONS ==========

String everyThreeHours() {
	return "H H/3 * * *"
}
