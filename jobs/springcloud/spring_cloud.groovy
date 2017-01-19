package springcloud

import io.springframework.cloud.ci.*
import io.springframework.cloud.compatibility.ManualBootCompatibilityBuildMaker
import io.springframework.cloud.compatibility.ClusterCompatibilityBuildMaker
import io.springframework.cloud.compatibility.CompatibilityBuildMaker
import io.springframework.cloud.compatibility.ConsulCompatibilityBuildMaker
import io.springframework.cloud.e2e.*
import io.springframework.cloud.f2f.SpringCloudPipelinesGradleBuildMaker
import io.springframework.cloud.f2f.SpringCloudPipelinesMavenBuildMaker
import io.springframework.cloud.sonar.ClusterSonarBuildMaker
import io.springframework.cloud.sonar.ConsulSonarBuildMaker
import io.springframework.cloud.sonar.SonarBuildMaker
import javaposse.jobdsl.dsl.DslFactory

import static io.springframework.cloud.common.AllCloudJobs.*
import static io.springframework.cloud.compatibility.CompatibilityBuildMaker.COMPATIBILITY_BUILD_DEFAULT_SUFFIX

DslFactory dsl = this

println "Projects with tests $ALL_JOBS_WITH_TESTS"
println "Projects without tests $JOBS_WITHOUT_TESTS"
println "Projects to build for automatic compatibility check $DEFAULT_BOOT_COMPATIBILITY_BUILD_JOBS"
println "Projects with branches to build for automatic compatibility check $JOBS_WITH_BRANCHES_FOR_COMPATIBILITY_BUILD"

// AUTOMATIC COMPATIBILITY BUILDS
(DEFAULT_BOOT_COMPATIBILITY_BUILD_JOBS).each { String projectName->
	new CompatibilityBuildMaker(dsl).build(projectName, everyThreeHours())
}
(JOBS_WITH_BRANCHES_FOR_COMPATIBILITY_BUILD).each { String projectName, List<String> branches ->
	branches.each { String branch ->
		new CompatibilityBuildMaker(dsl).build("${projectName}-${branch}", projectName, branch, everyThreeHours())
	}
}
JOBS_WITHOUT_TESTS.each {
	new CompatibilityBuildMaker(dsl).buildWithoutTests(it, everyThreeHours())
}
new CompatibilityBuildMaker(dsl, COMPATIBILITY_BUILD_DEFAULT_SUFFIX, 'spring-cloud-samples')
		.build('tests', everyThreeHours())
new ConsulCompatibilityBuildMaker(dsl).build(everyThreeHours())
new ClusterCompatibilityBuildMaker(dsl).build(everyThreeHours())
new CompatibilityBuildMaker(dsl).build("spring-cloud-contract", everyThreeHours())

// MANUAL COMPATIBILITY BUILD
new ManualBootCompatibilityBuildMaker(dsl).build()

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
// Brixton and Camden branches for Spring Cloud Release
branchMaker.deploy('spring-cloud-release', 'Brixton', false)
branchMaker.deploy('spring-cloud-release', 'Camden.x', false)

new ConsulSpringCloudDeployBuildMaker(dsl).deploy()
new ClusterSpringCloudDeployBuildMaker(dsl).deploy()
// CI BUILDS FOR INCUBATOR
new SpringCloudKubernetesDeployBuildMaker(dsl).deploy()
new VaultSpringCloudDeployBuildMaker(dsl).deploy()
// CI BUILDS FOR SPRING CLOUD CONTRACTS
new SpringCloudContractDeployBuildMaker(dsl).with {
	deploy(masterBranch())
	deploy("1.0.x")
}
// issue #159
new SpringCloudSamplesEndToEndBuildMaker(dsl, "marcingrzejszczak").build("spring-cloud-contract-159", everyThreeHours())

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
// All jobs for e2e with Brewery
new BrixtonBreweryEndToEndBuildMaker(dsl).build()
new CamdenBreweryEndToEndBuildMaker(dsl).build()
new SpringCloudSamplesEndToEndBuildMaker(dsl).with {
	buildWithGradleAndMavenTests("spring-cloud-contract-samples", everySixHours())
}
new SpringCloudSamplesEndToEndBuildMaker(dsl).with {
	buildWithGradleAndMavenTests("spring-cloud-contract-samples", everySixHours(), "1.0.x")
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
// TODO: Fix Consul Sonar Build
new ConsulSonarBuildMaker(dsl).buildSonar()
new ClusterSonarBuildMaker(dsl).buildSonar()

// F2F
new SpringCloudPipelinesMavenBuildMaker(dsl).build('github-webhook')
new SpringCloudPipelinesGradleBuildMaker(dsl).build('github-analytics')

// ========== FUNCTIONS ==========

String everyThreeHours() {
	return "H H/3 * * *"
}
