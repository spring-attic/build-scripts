package io.springframework.springcloudstream.ci

import io.springframework.common.Cron
import io.springframework.common.JdkConfig
import io.springframework.common.Maven
import io.springframework.common.TestPublisher
import io.springframework.springcloudstream.common.SpringCloudStreamJobs
import javaposse.jobdsl.dsl.DslFactory
/**
 * @author Soby Chacko
 */
class SpringCloudStreamBuildMarker implements JdkConfig, TestPublisher,
        Cron, SpringCloudStreamJobs, Maven {

    private final DslFactory dsl
    final String organization
    final String project

    String branchToBuild = "master"

    Map<String, Object> envVariables = new HashMap<>();

    SpringCloudStreamBuildMarker(DslFactory dsl, String organization, String project, String branchToBuild, Map<String, Object> envVariables) {
        this.dsl = dsl
        this.organization = organization
        this.project = project
        this.branchToBuild = branchToBuild
        this.envVariables = envVariables
    }

    SpringCloudStreamBuildMarker(DslFactory dsl, String organization, String project, Map<String, Object> envVariables) {
        this.dsl = dsl
        this.organization = organization
        this.project = project
        this.envVariables = envVariables
    }

    SpringCloudStreamBuildMarker(DslFactory dsl, String organization, String project) {
        this.dsl = dsl
        this.organization = organization
        this.project = project
    }

    void deploy(boolean checkTests = true, String mvnGoals = "clean deploy -U") {
        dsl.job("${prefixJob(project)}-${branchToBuild}-ci") {
            triggers {
                githubPush()
            }
            jdk jdk8()
            wrappers {
                colorizeOutput()
                environmentVariables(envVariables)
            }
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
                    goals(mvnGoals)
                }
            }
            publishers {
                if (checkTests) {
                    archiveJunit mavenJUnitResults()
                }
            }
        }
    }
}
