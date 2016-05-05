///*
package springcloud

import io.springframework.cloud.ci.ConsulSpringCloudDeployBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this
//
//def allProjects = ['spring-cloud-sleuth', 'spring-cloud-netflix', 'spring-cloud-zookeeper', 'spring-cloud-consul',
//				   'spring-cloud-bus', 'spring-cloud-commons', 'spring-cloud-config', 'spring-cloud-security',
//				   'spring-cloud-cloudfoundry', 'spring-cloud-aws', 'spring-cloud-build', 'spring-cloud-cluster']
//
//def projectsWithTests = allProjects - 'spring-cloud-build'
//
//// COMPATIBILITY BUILDS
//(allProjects - ['spring-cloud-consul', 'spring-cloud-build']).each { String projectName->
//	new CompatibilityBuildMaker(dsl).build(projectName, everyThreeHours())
//}
//new CompatibilityBuildMaker(dsl).buildWithoutTests('spring-cloud-build', everyThreeHours())
//new ConsulCompatibilityBuildMaker(dsl).build('spring-cloud-consul')
//
//// BENCHMARK BUILDS
//new BenchmarksBuildMaker(dsl).buildSleuth()
//
//// CI BUILDS
//new DocsAppBuildMaker(dsl).buildDocs(everyThreeHours())
//new SpringCloudDeployBuildMaker(dsl).with { SpringCloudDeployBuildMaker maker ->
//	(projectsWithTests - 'spring-cloud-consul').each {
//		maker.deploy(it)
//	}
//	maker.deployWithoutTests('spring-cloud-build')
//}
new ConsulSpringCloudDeployBuildMaker(dsl).deploy()
//
//// E2E BUILDS
//['spring-cloud-netflix', 'spring-cloud-zookeeper', 'spring-cloud-consul'].each { String projectName ->
//	new EndToEndBuildMaker(dsl).build(projectName, everyThreeHours())
//}
//new SleuthEndToEndBuildMaker(dsl).with {
//	buildSleuth(everyThreeHours())
//	buildSleuthStream(everyThreeHours())
//}
//
//// E2E on CF
//new CloudFoundryEndToEndBuildMaker(dsl).with {
//	buildBreweryForDocs()
//	buildSleuthDocApps()
//	buildSpringCloudStream()
//}
//
//// SONAR BUILDS - disabled until sonar is set
//*/
///*['spring-cloud-bus', 'spring-cloud-commons', 'spring-cloud-sleuth', 'spring-cloud-netflix', 'spring-cloud-zookeeper'].each {
//	new SonarBuildMaker(dsl).buildSonar(it)
//}
//new ConsulSonarBuildMaker(dsl).buildSonar()*//*
//
//
//// F2F
//new AppDeployingBuildMaker(dsl).with {
//	build('marcingrzejszczak', 'atom-feed')
//	build('dsyer', 'github-analytics')
//}
//
//// ========== FUNCTIONS ==========
//
//String everyThreeHours() {
//	return "H H/3 * * *"
//}*/
