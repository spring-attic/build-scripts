package org.springframework.jenkins.springcloudstream.ci

import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.TestPublisher
import org.springframework.jenkins.springcloudstream.common.SpringCloudStreamJobs
import javaposse.jobdsl.dsl.DslFactory

import static org.springframework.jenkins.common.job.Artifactory.artifactoryMaven3Configurator
import static org.springframework.jenkins.common.job.Artifactory.artifactoryMavenBuild

/**
 * @author Soby Chacko
 */
class SpringCloudStreamBuildMarker implements JdkConfig, TestPublisher,
        Cron, SpringCloudStreamJobs, Maven {

    private final DslFactory dsl
    final String organization
    final String project

    String branchToBuild

    Map<String, Object> envVariables = new HashMap<>()

    boolean ghPushTrigger = true

    SpringCloudStreamBuildMarker(DslFactory dsl, String organization, String project, String branchToBuild, Map<String, Object> envVariables) {
        this.dsl = dsl
        this.organization = organization
        this.project = project
        this.branchToBuild = branchToBuild
        this.envVariables = envVariables
    }

    SpringCloudStreamBuildMarker(DslFactory dsl, String organization, String project, String branchToBuild = "master", boolean ghPushTrigger = true) {
        this.dsl = dsl
        this.organization = organization
        this.project = project
        this.branchToBuild = branchToBuild
        this.ghPushTrigger = ghPushTrigger
    }

    void deploy(boolean checkTests = true, boolean recurseSubmodules = false, String mvnGoals = "clean deploy -U -Pfull,spring",
                String scriptDir = null, String startScript = null, String stopScript = null, boolean docsBuild = false) {
        dsl.job("${prefixJob(project)}-${branchToBuild}-ci") {
            if (ghPushTrigger) {
                triggers {
                    githubPush()
                }
            }
            jdk jdk8()
            wrappers {
                colorizeOutput()
                environmentVariables(envVariables)
                timeout {
                    noActivity(300)
                    failBuild()
                    writeDescription('Build failed due to timeout after {0} minutes of inactivity')
                }
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
                if (checkTests) {
                    archiveJunit mavenJUnitResults()
                }
                mailer('scdf-ci@pivotal.io', true, true)
            }
        }
    }
}
