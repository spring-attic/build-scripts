package io.springframework.scstappstarters.common

import groovy.transform.CompileStatic
import io.springframework.common.Cron
import io.springframework.common.JdkConfig
import io.springframework.common.Maven
import io.springframework.common.TestPublisher
import javaposse.jobdsl.dsl.DslFactory

import static io.springframework.common.Artifactory.artifactoryMaven3Configurator
import static io.springframework.common.Artifactory.artifactoryMavenBuild

/**
 * @author Marcin Grzejszczak
 */
@CompileStatic
class SpringScstAppStartersBuildMaker implements JdkConfig, TestPublisher,
        Cron, SpringScstAppStarterJobs, Maven {

    private final DslFactory dsl
    final String organization
    final String project
    
    final String branchToBuild = "master"

    SpringScstAppStartersBuildMaker(DslFactory dsl, String organization, String project) {
        this.dsl = dsl
        this.organization = organization
        this.project = project
    }

    void deploy() {
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
                    mavenVersion(maven33())
                    goals('clean install')
                }
                artifactoryMaven3Configurator(it as Node)
            }
            //ENABLE ONCE WE HAVE TESTS
//				publishers {
//					archiveJunit mavenJUnitResults()
//					archiveJunit mavenJUnitFailsafeResults()
//				}
        }

    }
}
