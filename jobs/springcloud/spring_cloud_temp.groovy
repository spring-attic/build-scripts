package springcloud

import io.springframework.cloud.compatibility.ClusterCompatibilityBuildMaker
import io.springframework.cloud.compatibility.CompatibilityBuildMaker
import io.springframework.cloud.compatibility.ConsulCompatibilityBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

def allProjects = ['spring-cloud-sleuth', 'spring-cloud-netflix', 'spring-cloud-zookeeper', 'spring-cloud-consul',
				   'spring-cloud-bus', 'spring-cloud-commons', 'spring-cloud-config', 'spring-cloud-security',
				   'spring-cloud-cloudfoundry', 'spring-cloud-aws', 'spring-cloud-build', 'spring-cloud-cluster']

def projectsWithTests = allProjects - 'spring-cloud-build'

// COMPATIBILITY BUILDS
(allProjects - ['spring-cloud-consul', 'spring-cloud-build', 'spring-cloud-cluster']).each { String projectName->
	new CompatibilityBuildMaker(dsl).build(projectName, everyThreeHours())
}
new CompatibilityBuildMaker(dsl).buildWithoutTests('spring-cloud-build', everyThreeHours())
new ConsulCompatibilityBuildMaker(dsl).build()
new ClusterCompatibilityBuildMaker(dsl).build()

// ========== FUNCTIONS ==========

String everyThreeHours() {
	return "H H/3 * * *"
}
