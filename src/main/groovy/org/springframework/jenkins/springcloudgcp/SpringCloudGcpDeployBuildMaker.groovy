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

    String cleanAndDeploy(boolean isRelease) {
        return isRelease ? """
					#!/bin/bash -x

			   		lines=\$(find . -type f -name pom.xml | xargs grep SNAPSHOT | grep -v ".contains(" | grep -v regex | wc -l)
					if [ \$lines -eq 0 ]; then
						./mvnw clean deploy -U -Pspring
					else
						echo "Snapshots found. Aborting the release build."
					fi
			   """
                    :
                """
                    ./mvnw clean deploy -U
                """

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
                shell(cleanAndDeploy(isRelease))
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
                mailer('schacko@pivotal.io,meltsufin@google.com,joaomartins@google.com,abilan@pivotal.io', true, true)
                //archiveJunit mavenJUnitResults()
                //archiveJunit mavenJUnitFailsafeResults()
            }
        }
    }
}
