package springcloud

import io.springframework.cloud.ci.ConsulSpringCloudDeployBuildMaker
import io.springframework.cloud.ci.DocsAppBuildMaker
import io.springframework.cloud.ci.SleuthBenchmarksBuildMaker
import io.springframework.cloud.ci.SleuthMemoryBenchmarksBuildMaker
import io.springframework.cloud.ci.SpringCloudBranchBuildMaker
import io.springframework.cloud.ci.SpringCloudContractDeployBuildMaker
import io.springframework.cloud.ci.SpringCloudDeployBuildMaker
import io.springframework.cloud.ci.SpringCloudGatewayDeployBuildMaker
import io.springframework.cloud.ci.SpringCloudKubernetesDeployBuildMaker
import io.springframework.cloud.ci.SpringCloudPipelinesDeployBuildMaker
import io.springframework.cloud.ci.VaultSpringCloudDeployBuildMaker
import io.springframework.cloud.compatibility.BootCompatibilityBuildMaker
import io.springframework.cloud.compatibility.ConsulCompatibilityBuildMaker
import io.springframework.cloud.compatibility.ManualBootCompatibilityBuildMaker
import io.springframework.cloud.compatibility.ManualSpringCompatibilityBuildMaker
import io.springframework.cloud.compatibility.SpringCompatibilityBuildMaker
import io.springframework.cloud.e2e.CamdenBreweryEndToEndBuildMaker
import io.springframework.cloud.e2e.CloudFoundryBreweryTestExecutor
import io.springframework.cloud.e2e.CloudFoundryEndToEndBuildMaker
import io.springframework.cloud.e2e.EndToEndBuildMaker
import io.springframework.cloud.e2e.JoshEndToEndBuildMaker
import io.springframework.cloud.e2e.NetflixEndToEndBuildMaker
import io.springframework.cloud.e2e.SleuthEndToEndBuildMaker
import io.springframework.cloud.e2e.SpringCloudSamplesEndToEndBuildMaker
import io.springframework.cloud.e2e.SpringCloudSamplesTestsBuildMaker
import io.springframework.cloud.f2f.SpringCloudPipelinesGradleBuildMaker
import io.springframework.cloud.f2f.SpringCloudPipelinesMavenBuildMaker
import io.springframework.cloud.sonar.ConsulSonarBuildMaker
import io.springframework.cloud.sonar.SonarBuildMaker
import javaposse.jobdsl.dsl.DslFactory

import static BootCompatibilityBuildMaker.COMPATIBILITY_BUILD_DEFAULT_SUFFIX
import static io.springframework.cloud.common.AllCloudJobs.ALL_DEFAULT_JOBS
import static io.springframework.cloud.common.AllCloudJobs.ALL_JOBS_WITH_TESTS
import static io.springframework.cloud.common.AllCloudJobs.DEFAULT_BOOT_COMPATIBILITY_BUILD_JOBS
import static io.springframework.cloud.common.AllCloudJobs.DEFAULT_SPRING_COMPATIBILITY_BUILD_JOBS
import static io.springframework.cloud.common.AllCloudJobs.JOBS_WITHOUT_TESTS
import static io.springframework.cloud.common.AllCloudJobs.JOBS_WITH_BRANCHES
import static io.springframework.cloud.common.AllCloudJobs.JOBS_WITH_BRANCHES_FOR_COMPATIBILITY_BUILD
import static io.springframework.cloud.compatibility.SpringCompatibilityBuildMaker.COMPATIBILITY_BUILD_SPRING_SUFFIX

DslFactory dsl = this

println "Projects with tests $ALL_JOBS_WITH_TESTS"
println "Projects without tests $JOBS_WITHOUT_TESTS"
println "Projects to build for automatic compatibility check $DEFAULT_BOOT_COMPATIBILITY_BUILD_JOBS"
println "Projects with branches to build for automatic compatibility check $JOBS_WITH_BRANCHES_FOR_COMPATIBILITY_BUILD"

// AUTOMATIC COMPATIBILITY BUILDS
// BOOT
(DEFAULT_BOOT_COMPATIBILITY_BUILD_JOBS).each { String projectName->
	new BootCompatibilityBuildMaker(dsl).buildWithoutTests(projectName, everyThreeHours())
}
(JOBS_WITH_BRANCHES_FOR_COMPATIBILITY_BUILD).each { String projectName, List<String> branches ->
	branches.each { String branch ->
		new BootCompatibilityBuildMaker(dsl).buildWithoutTests("${projectName}-${branch}", projectName, branch, every12Hours())
	}
}
JOBS_WITHOUT_TESTS.each {
	new BootCompatibilityBuildMaker(dsl).buildWithoutTests(it, everyThreeHours())
}
new BootCompatibilityBuildMaker(dsl, COMPATIBILITY_BUILD_DEFAULT_SUFFIX, 'spring-cloud-samples')
		.buildWithoutTests('tests', everyThreeHours())
new ConsulCompatibilityBuildMaker(dsl).buildWithoutTestsForBoot(everyThreeHours())
new BootCompatibilityBuildMaker(dsl).buildWithoutTests("spring-cloud-contract", everyThreeHours())

// SPRING
(DEFAULT_SPRING_COMPATIBILITY_BUILD_JOBS).each { String projectName->
	new SpringCompatibilityBuildMaker(dsl).buildWithoutTests(projectName, everyDay())
}
JOBS_WITHOUT_TESTS.each {
	new SpringCompatibilityBuildMaker(dsl).buildWithoutTests(it, everyDay())
}
new SpringCompatibilityBuildMaker(dsl, COMPATIBILITY_BUILD_SPRING_SUFFIX, 'spring-cloud-samples')
		.buildWithoutTests('tests', everyDay())
new ConsulCompatibilityBuildMaker(dsl).buildWithoutTestsForSpring(everyDay())
new SpringCompatibilityBuildMaker(dsl).buildWithoutTests("spring-cloud-contract", everyDay())

// MANUAL COMPATIBILITY BUILD
new ManualBootCompatibilityBuildMaker(dsl).build()
new ManualSpringCompatibilityBuildMaker(dsl).build()

// BENCHMARK BUILDS
new SleuthBenchmarksBuildMaker(dsl).buildSleuth()
new SleuthMemoryBenchmarksBuildMaker(dsl).buildSleuth()

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
// TODO: Remove once Dalston is done
//branchMaker.deploy('spring-cloud-release', 'Brixton', false)
branchMaker.deploy('spring-cloud-release', 'Camden.x', false)

new ConsulSpringCloudDeployBuildMaker(dsl).deploy()
// CI BUILDS FOR INCUBATOR
new SpringCloudKubernetesDeployBuildMaker(dsl).deploy()
new SpringCloudGatewayDeployBuildMaker(dsl).deploy()
new VaultSpringCloudDeployBuildMaker(dsl).deploy()
// CI BUILDS FOR SPRING CLOUD CONTRACTS
new SpringCloudContractDeployBuildMaker(dsl).with {
	deploy(masterBranch())
	deploy("1.0.x")
	branch()
}
// issue #159
new SpringCloudSamplesEndToEndBuildMaker(dsl, "marcingrzejszczak").build("spring-cloud-contract-159", everyThreeHours())
new SpringCloudSamplesEndToEndBuildMaker(dsl, "openzipkin").buildWithoutTests("sleuth-webmvc-example", everyThreeHours())

// E2E BUILDS
new NetflixEndToEndBuildMaker(dsl).with {
	build(everySixHours())
}

// CUSTOM E2E FOR SPRING CLOUD PROJECTS
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
// TODO: Remove once Dalston is done
//new BrixtonBreweryEndToEndBuildMaker(dsl).build()
new CamdenBreweryEndToEndBuildMaker(dsl).build()
new CamdenBreweryEndToEndBuildMaker(dsl).buildForLatestBoot()
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
	// TODO: Remove once Dalston is done
//	build('bootiful-microservices-brixton',
//			'scripts/scenario_brixton_tester.sh',
//			everyThreeHours(),
//			'scripts/kill_all.sh')
	build('bootiful-microservices-camden',
			'scripts/scenario_camden_tester.sh',
			everyThreeHours(),
			'scripts/kill_all.sh')
}
new SpringCloudSamplesTestsBuildMaker(dsl).with {
	buildForCamden()
	buildForDalston()
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

// SONAR

['spring-cloud-bus', 'spring-cloud-commons', 'spring-cloud-sleuth', 'spring-cloud-netflix',
 'spring-cloud-zookeeper', 'spring-cloud-contract'].each {
	new SonarBuildMaker(dsl).buildSonar(it)
}
// TODO: Fix Consul Sonar Build
new ConsulSonarBuildMaker(dsl).buildSonar()

// F2F
new SpringCloudPipelinesMavenBuildMaker(dsl).build('github-webhook')
new SpringCloudPipelinesGradleBuildMaker(dsl).build('github-analytics')

// ========== FUNCTIONS ==========

String everyThreeHours() {
	return "H H/3 * * *"
}

String every12Hours() {
	return "H H/12 * * *"
}

String everyDay() {
	return "H H * * *"
}
