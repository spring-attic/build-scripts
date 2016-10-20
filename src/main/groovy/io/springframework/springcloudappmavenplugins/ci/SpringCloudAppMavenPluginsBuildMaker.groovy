package io.springframework.springcloudappmavenplugins.ci

import io.springframework.common.Cron
import io.springframework.common.JdkConfig
import io.springframework.common.Maven
import io.springframework.common.TestPublisher
import io.springframework.springcloudappmavenplugins.common.SpringCloudAppMavenPluginsJobs
import javaposse.jobdsl.dsl.DslFactory
/**
 * @author Soby Chacko
 */
class SpringCloudAppMavenPluginsBuildMaker implements JdkConfig, TestPublisher,
        Cron, SpringCloudAppMavenPluginsJobs, Maven {

    private final DslFactory dsl
    final String organization
    final String project

    String branchToBuild = "master"

    Map<String, Object> envVariables = new HashMap<>();

    SpringCloudAppMavenPluginsBuildMaker(DslFactory dsl, String organization, String project) {
        this.dsl = dsl
        this.organization = organization
        this.project = project
    }

    void deploy() {
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
                    goals('clean deploy -U')
                }
            }
            publishers {
                archiveJunit mavenJUnitResults()
            }
        }
    }
}
