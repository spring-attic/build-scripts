package springcloud

import org.springframework.jenkins.cloud.ci.ConsulSpringCloudDeployBuildMaker
import org.springframework.jenkins.cloud.ci.DocsAppBuildMaker
import org.springframework.jenkins.cloud.ci.SleuthBenchmarksBuildMaker
import org.springframework.jenkins.cloud.ci.SleuthMemoryBenchmarksBuildMaker
import org.springframework.jenkins.cloud.ci.SpringCloudBranchBuildMaker
import org.springframework.jenkins.cloud.ci.SpringCloudContractDeployBuildMaker
import org.springframework.jenkins.cloud.ci.SpringCloudDeployBuildMaker
import org.springframework.jenkins.cloud.ci.SpringCloudGatewayDeployBuildMaker
import org.springframework.jenkins.cloud.ci.SpringCloudKubernetesDeployBuildMaker
import org.springframework.jenkins.cloud.ci.SpringCloudPipelinesDeployBuildMaker
import org.springframework.jenkins.cloud.ci.SpringCloudReleaseToolsBuildMaker
import org.springframework.jenkins.cloud.ci.VaultSpringCloudDeployBuildMaker
import org.springframework.jenkins.cloud.compatibility.BootCompatibilityBuildMaker
import org.springframework.jenkins.cloud.compatibility.ConsulCompatibilityBuildMaker
import org.springframework.jenkins.cloud.compatibility.ManualBootCompatibilityBuildMaker
import org.springframework.jenkins.cloud.compatibility.ManualSpringCompatibilityBuildMaker
import org.springframework.jenkins.cloud.compatibility.SpringCompatibilityBuildMaker
import org.springframework.jenkins.cloud.e2e.CloudFoundryBreweryTestExecutor
import org.springframework.jenkins.cloud.e2e.CloudFoundryEndToEndBuildMaker
import org.springframework.jenkins.cloud.e2e.DalstonBreweryEndToEndBuildMaker
import org.springframework.jenkins.cloud.e2e.EndToEndBuildMaker
import org.springframework.jenkins.cloud.e2e.JoshEndToEndBuildMaker
import org.springframework.jenkins.cloud.e2e.NetflixEndToEndBuildMaker
import org.springframework.jenkins.cloud.e2e.SleuthEndToEndBuildMaker
import org.springframework.jenkins.cloud.e2e.SpringCloudSamplesEndToEndBuildMaker
import org.springframework.jenkins.cloud.e2e.SpringCloudSamplesTestsBuildMaker
import org.springframework.jenkins.cloud.f2f.SpringCloudPipelinesGradleBuildMaker
import org.springframework.jenkins.cloud.f2f.SpringCloudPipelinesMavenBuildMaker
import org.springframework.jenkins.cloud.sonar.ConsulSonarBuildMaker
import org.springframework.jenkins.cloud.sonar.SonarBuildMaker
import javaposse.jobdsl.dsl.DslFactory

import static BootCompatibilityBuildMaker.COMPATIBILITY_BUILD_DEFAULT_SUFFIX
import static org.springframework.jenkins.cloud.common.AllCloudJobs.ALL_DEFAULT_JOBS
import static org.springframework.jenkins.cloud.common.AllCloudJobs.ALL_JOBS_WITH_TESTS
import static org.springframework.jenkins.cloud.common.AllCloudJobs.DEFAULT_BOOT_COMPATIBILITY_BUILD_JOBS
import static org.springframework.jenkins.cloud.common.AllCloudJobs.DEFAULT_SPRING_COMPATIBILITY_BUILD_JOBS
import static org.springframework.jenkins.cloud.common.AllCloudJobs.JOBS_WITHOUT_TESTS
import static org.springframework.jenkins.cloud.common.AllCloudJobs.JOBS_WITH_BRANCHES
import static org.springframework.jenkins.cloud.common.AllCloudJobs.JOBS_WITH_BRANCHES_FOR_COMPATIBILITY_BUILD
import static SpringCompatibilityBuildMaker.COMPATIBILITY_BUILD_SPRING_SUFFIX

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
new SpringCloudReleaseToolsBuildMaker(dsl).deploy()

// BRANCHES BUILD - spring-cloud organization
// Build that allows you to deploy, and build gh-pages of multiple branches. Used for projects
// where we support multiple versions
def branchMaker = new SpringCloudDeployBuildMaker(dsl)
JOBS_WITH_BRANCHES.each { String project, List<String> branches ->
	branches.each { String branch ->
		boolean checkTests = !JOBS_WITHOUT_TESTS.contains(project)
		branchMaker.deploy(project, branch, checkTests)
	}
}
// Release branches for Spring Cloud Release
// TODO: Remove once Edgware  is done
//branchMaker.deploy('spring-cloud-release', 'Camden', false)
branchMaker.deploy('spring-cloud-release', 'Dalston', false)

new ConsulSpringCloudDeployBuildMaker(dsl).deploy()
// CI BUILDS FOR INCUBATOR
new SpringCloudKubernetesDeployBuildMaker(dsl).deploy()
new SpringCloudGatewayDeployBuildMaker(dsl).deploy()
new VaultSpringCloudDeployBuildMaker(dsl).with {
	deploy(masterBranch())
	deploy('1.0.x')
}
new SpringCloudDeployBuildMaker(dsl, "spring-cloud-incubator").deploy("spring-cloud-contract-raml")
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
// TODO: Remove once Edgware is done
//new CamdenBreweryEndToEndBuildMaker(dsl).build()
//new CamdenBreweryEndToEndBuildMaker(dsl).buildForLatestBoot()
new DalstonBreweryEndToEndBuildMaker(dsl).build()
new DalstonBreweryEndToEndBuildMaker(dsl).buildForLatestBoot()
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
	// TODO: Remove once Edgware is done
	build('bootiful-microservices-dalston',
			'scripts/scenario_dalston_tester.sh',
			everyThreeHours(),
			'scripts/kill_all.sh')
	build('bootiful-microservices-edgware',
			'scripts/scenario_edgware_tester.sh',
			everyThreeHours(),
			'scripts/kill_all.sh')
}
new SpringCloudSamplesTestsBuildMaker(dsl).with {
	buildForDalston()
	buildForEdgware()
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
