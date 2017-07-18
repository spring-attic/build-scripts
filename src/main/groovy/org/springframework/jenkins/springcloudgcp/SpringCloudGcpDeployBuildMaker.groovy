package org.springframework.jenkins.springcloudgcp

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.common.job.*

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

    void deploy() {
        String project = 'spring-cloud-gcp'
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
                    mavenInstallation(maven33())
                    goals('clean deploy -U')
                }
            }
            publishers {
                mailer('schacko@pivotal.io', true, true)
                //archiveJunit mavenJUnitResults()
                //archiveJunit mavenJUnitFailsafeResults()
            }
        }
    }
}
