package org.springframework.jenkins.springclouddataflowsamples

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.TestPublisher

import static org.springframework.jenkins.common.job.Artifactory.artifactoryMaven3Configurator
import static org.springframework.jenkins.common.job.Artifactory.artifactoryMavenBuild
/**
 * @author Soby Chacko
 */
class SpringCloudDataFlowSamplesBuildMaker implements JdkConfig, TestPublisher,
        Cron, SpringCloudDataFlowSamplesJobs, Maven {

    private final String branchToBuild = "master"

    private final DslFactory dsl
    final String organization

    SpringCloudDataFlowSamplesBuildMaker (DslFactory dsl) {
        this.dsl = dsl
        this.organization = 'spring-cloud'
    }

    void deploy() {
        String project = 'spring-cloud-dataflow-samples'
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
            }
            configure {
                artifactoryMavenBuild(it as Node) {
                    mavenVersion(maven33())
                    goals('clean install -U -Pfull -Pspring')
                }
                artifactoryMaven3Configurator(it as Node) {
                }
            }
            publishers {
                mailer('schacko@pivotal.io', true, true)
            }
        }
    }
}
