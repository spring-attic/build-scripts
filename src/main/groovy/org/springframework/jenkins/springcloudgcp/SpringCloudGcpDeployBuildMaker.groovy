package org.springframework.jenkins.springcloudgcp

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.common.job.*

import static org.springframework.jenkins.common.job.Artifactory.artifactoryMaven3Configurator
import static org.springframework.jenkins.common.job.Artifactory.artifactoryMavenBuild


/**
 * @author Marcin Grzejszczak
 */
class SpringCloudGcpDeployBuildMaker implements JdkConfig, TestPublisher,
        Cron, SpringCloudGcpJobs, Maven {

    private final String branchToBuild = "master"

    private final DslFactory dsl
    final String organization

    SpringCloudGcpDeployBuildMaker(DslFactory dsl) {
        this.dsl = dsl
        this.organization = 'spring-cloud'
    }

    void deploy(boolean isRelease = false, String releaseType) {
        String project = 'spring-cloud-gcp'
        dsl.job("${prefixJob(project)}-$branchToBuild-ci") {
            triggers {
                if (!isRelease){
                    githubPush()
                }
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
                    mavenInstallation(maven33())
                    goals('clean deploy -U')
                }
            }
            configure {

                artifactoryMavenBuild(it as Node) {
                    mavenVersion(maven33())
                    if (releaseType != null && releaseType.equals("milestone")) {
                        goals('clean install -U -Pfull -Pspring -Pmilestone -pl :spring-cloud-gcp-docs')
                    }
                    else {
                        goals('clean install -U -Pfull -Pspring -pl :spring-cloud-gcp-docs')
                    }
                }
                artifactoryMaven3Configurator(it as Node) {
                    if (releaseType != null && releaseType.equals("milestone")) {
                        deployReleaseRepository("libs-milestone-local")
                    }
                }






//                artifactoryMavenBuild(it as Node) {
//                    mavenVersion(maven35())
//                    goals('clean install -U -Pfull -Pspring')
//                }
//                artifactoryMaven3Configurator(it as Node)


            }
            publishers {
                mailer('schacko@pivotal.io', true, true)
                //archiveJunit mavenJUnitResults()
                //archiveJunit mavenJUnitFailsafeResults()
            }
        }
    }
}
