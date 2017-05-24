package io.springframework.dataflowacceptancetests.ci

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
class ScdfAcceptanceTestsBuildMaker implements JdkConfig, TestPublisher,
        Cron, SpringCloudStreamJobs, Maven {

    private final DslFactory dsl
    final String organization
    final String project

    String branchToBuild = "master"

    Map<String, Object> envVariables = new HashMap<>()

    boolean ghPushTrigger = true

    ScdfAcceptanceTestsBuildMaker(DslFactory dsl, String organization, String project, String branchToBuild, Map<String, Object> envVariables) {
        this.dsl = dsl
        this.organization = organization
        this.project = project
        this.branchToBuild = branchToBuild
        this.envVariables = envVariables
    }

    ScdfAcceptanceTestsBuildMaker(DslFactory dsl, String organization, String project, String branchToBuild = "master", boolean ghPushTrigger = true) {
        this.dsl = dsl
        this.organization = organization
        this.project = project
        this.branchToBuild = branchToBuild
        this.ghPushTrigger = ghPushTrigger
    }

    void deploy(boolean checkTests = true, String scriptDir, String platform, String binder, String tests) {
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
                }
            }
            steps {
                if (scriptDir != null && startScript != null) {
                    shell(scriptToExecute(scriptDir, startScript))
                }
//                maven {
//                    mavenInstallation(maven35())
//                    goals(mvnGoals)
//                }
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
