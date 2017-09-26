package org.springframework.jenkins.springcloudappstartermavenplugins.ci

import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.springcloudappstartermavenplugins.common.SpringCloudAppStarterMavenPluginsJobs
import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.TestPublisher

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

    void deploy(boolean checkTests = false, boolean isGaRelease = false) {
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
                credentialsBinding {
                    file('FOO_SEC', "spring-signing-secring.gpg")
                    file('FOO_PUB', "spring-signing-pubring.gpg")
                    string('FOO_PASSPHRASE', "spring-gpg-passphrase")
                    usernamePassword('SONATYPE_USER', 'SONATYPE_PASSWORD', "oss-token")
                }
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
                shell(cleanAndDeploy(project, isGaRelease))
            }
            if (checkTests) {
                publishers {
                    archiveJunit mavenJUnitResults()
                }
            }
        }
    }
}
