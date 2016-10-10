package io.springframework.scstappstarters.common

import io.springframework.common.*
import javaposse.jobdsl.dsl.DslFactory

import static io.springframework.common.Artifactory.artifactoryMavenBuild

/**
 * @author Marcin Grzejszczak
 */
class SpringScstAppStartersBuildMaker implements JdkConfig, TestPublisher,
		Cron, SpringScstAppStarterJobs, Maven {

	private static final List<String> BRANCHES_TO_BUILD = ['master']

	private final DslFactory dsl
	final String organization

	SpringScstAppStartersBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = 'sobychacko'
	}

	SpringScstAppStartersBuildMaker(DslFactory dsl, String organization) {
		this.dsl = dsl
		this.organization = organization
	}

	void deploy() {
		String project = 'spring-cloud-stream-app-starters-core'
		BRANCHES_TO_BUILD.each { String branchToBuild ->
			dsl.job("${prefixJob(project)}-$branchToBuild-ci") {
				triggers {
					githubPush()
				}
				jdk jdk8()
				scm {
					git {
						remote {
							url "https://github.com/${organization}/${project}"
							branch branchToBuild
						}
					}
				}
				steps {
					maven {
						mavenInstallation(maven32())
						goals('install -U -DskipTests')
					}
				}
				configure {
					artifactoryMavenBuild(it as Node) {
						mavenVersion(maven32())
						goals('clean install')
						rootPom('pom.xml')
						mavenOpts('-Xmx2g -XX:MaxPermSize=512m')
					}
					artifactoryMaven3Configurator(it as Node)
//					artifactoryMaven3Configurator(it as Node) {
//						excludePatterns('**/*-tests.jar,**/*-site.jar,**/*spring-boot-sample*,**/*spring-boot-integration-tests*,**/*.effective-pom,**/*-starter-poms.zip')
//					}
				}
				//ENABLE ONCE WE HAVE TESTS
//				publishers {
//					archiveJunit mavenJUnitResults()
//					archiveJunit mavenJUnitFailsafeResults()
//				}
			}
		}
	}
}
