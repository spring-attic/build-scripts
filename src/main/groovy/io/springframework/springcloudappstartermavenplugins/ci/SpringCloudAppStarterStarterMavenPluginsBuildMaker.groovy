package io.springframework.springcloudappstartermavenplugins.ci

import io.springframework.common.Cron
import io.springframework.common.JdkConfig
import io.springframework.common.Maven
import io.springframework.common.TestPublisher
import io.springframework.springcloudappstartermavenplugins.common.SpringCloudAppStarterMavenPluginsJobs
import javaposse.jobdsl.dsl.DslFactory
/**
 * @author Soby Chacko
 */
class SpringCloudAppStarterStarterMavenPluginsBuildMaker implements JdkConfig, TestPublisher,
        Cron, SpringCloudAppStarterMavenPluginsJobs, Maven {

    private final DslFactory dsl
    final String organization
    final String repo
    final String project

    String branchToBuild = "master"

    Map<String, Object> envVariables = new HashMap<>();

    SpringCloudAppStarterStarterMavenPluginsBuildMaker(DslFactory dsl, String organization, String repo) {
        this.dsl = dsl
        this.organization = organization
        this.repo = repo
    }

    SpringCloudAppStarterStarterMavenPluginsBuildMaker(DslFactory dsl, String organization, String repo, String project) {
        this.dsl = dsl
        this.organization = organization
        this.repo = repo
        this.project = project
    }

    void deploy(boolean checkTests = false) {
        String projectBranch
        if (project != null && !project.isEmpty()) {
            projectBranch = project + "-" + branchToBuild
        }
        else {
            projectBranch = branchToBuild
        }

        dsl.job("${prefixJob(repo)}-${projectBranch}-ci") {
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
                        url "https://github.com/${organization}/${repo}"
                        branch branchToBuild
                    }
                }
            }
            steps {
                shell(cleanAndDeploy(project))
            }
            if (checkTests) {
                publishers {
                    archiveJunit mavenJUnitResults()
                }
            }
        }
    }
}
