package io.springframework.springcloudstream.ci

import io.springframework.common.job.Cron
import io.springframework.common.job.JdkConfig
import io.springframework.common.job.Maven
import io.springframework.common.job.TestPublisher
import io.springframework.springcloudstream.common.SpringCloudStreamJobs
import javaposse.jobdsl.dsl.DslFactory

import static io.springframework.common.job.Artifactory.artifactoryMaven3Configurator
import static io.springframework.common.job.Artifactory.artifactoryMavenBuild

/**
 * @author Soby Chacko
 */
class SpringCloudStreamBuildMarker implements JdkConfig, TestPublisher,
        Cron, SpringCloudStreamJobs, Maven {

    private final DslFactory dsl
    final String organization
    final String project

    String branchToBuild = "master"

    Map<String, Object> envVariables = new HashMap<>()

    boolean ghPushTrigger = true
    boolean isRelease = false

    SpringCloudStreamBuildMarker(DslFactory dsl, String organization, String project, String branchToBuild, Map<String, Object> envVariables) {
        this.dsl = dsl
        this.organization = organization
        this.project = project
        this.branchToBuild = branchToBuild
        this.envVariables = envVariables
    }

    SpringCloudStreamBuildMarker(DslFactory dsl, String organization, String project, String branchToBuild) {
        this.dsl = dsl
        this.organization = organization
        this.project = project
        this.branchToBuild = branchToBuild
    }

    SpringCloudStreamBuildMarker(DslFactory dsl, String organization, String project, Map<String, Object> envVariables, boolean isRelease = false) {
        this.dsl = dsl
        this.organization = organization
        this.project = project
        this.envVariables = envVariables
        this.isRelease = isRelease
    }

    SpringCloudStreamBuildMarker(DslFactory dsl, String organization, String project, boolean isRelease = false, boolean ghPushTrigger = true) {
        this.dsl = dsl
        this.organization = organization
        this.project = project
        this.isRelease = isRelease
        this.ghPushTrigger = ghPushTrigger
    }

    void deploy(boolean checkTests = true, boolean recurseSubmodules = false, String mvnGoals = "clean deploy -U -Pfull,spring",
                String scriptDir = null, String startScript = null, String stopScript = null, boolean docsBuild = false) {
        dsl.job("${prefixJob(project)}-${branchToBuild}-ci") {
            if (ghPushTrigger && !isRelease) {
                triggers {
                    githubPush()
                }
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
                    extensions {
                        submoduleOptions {
                            if (recurseSubmodules) {
                                recursive()
                            }
                        }
                    }
                }
            }
            steps {
                if (scriptDir != null && startScript != null) {
                    shell(scriptToExecute(scriptDir, startScript))
                }
                maven {
                    mavenInstallation(maven35())
                    goals(mvnGoals)
                }
                if (scriptDir != null && stopScript != null) {
                    shell(scriptToExecute(scriptDir, stopScript))
                }
            }
            configure {
                if (docsBuild) {
                    artifactoryMavenBuild(it as Node) {
                        mavenVersion(maven35())
                        goals('clean install -U -Pfull -Pspring')
                    }
                    artifactoryMaven3Configurator(it as Node)
                }

            }
            publishers {
                mailer('scdf-ci@pivotal.io', true, true)
                if (checkTests) {
                    archiveJunit mavenJUnitResults()
                }
            }
        }
    }
}
