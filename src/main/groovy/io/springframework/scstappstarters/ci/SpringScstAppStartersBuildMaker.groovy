package io.springframework.scstappstarters.ci

import io.springframework.common.Cron
import io.springframework.common.JdkConfig
import io.springframework.common.Maven
import io.springframework.common.TestPublisher
import io.springframework.scstappstarters.common.SpringScstAppStarterJobs
import javaposse.jobdsl.dsl.DslFactory

import static io.springframework.common.Artifactory.artifactoryMaven3Configurator
import static io.springframework.common.Artifactory.artifactoryMavenBuild
/**
 * @author Marcin Grzejszczak
 */
class SpringScstAppStartersBuildMaker implements JdkConfig, TestPublisher,
        Cron, SpringScstAppStarterJobs, Maven {

    private final DslFactory dsl
    final String organization
    final String project

    final String branchToBuild = "master"

    SpringScstAppStartersBuildMaker(DslFactory dsl, String organization,
                                    String project) {
        this.dsl = dsl
        this.organization = organization
        this.project = project
    }

    void deploy(boolean buildApps = true, boolean checkTests = true,
                boolean dockerHubPush = true, boolean githubPushTrigger = true,
                boolean fullProfile = false) {
        dsl.job("${prefixJob(project)}-${branchToBuild}-ci") {
            if (githubPushTrigger) {
//                triggers {
//                    githubPush()
//                }
            }
            scm {
                git {
                    remote {
                        url "https://github.com/${organization}/${project}"
                        branch branchToBuild
                    }
                }
            }

            jdk jdk8()
            wrappers {
                colorizeOutput()
                maskPasswords()
                credentialsBinding {
                    usernamePassword('DOCKER_HUB_USERNAME', 'DOCKER_HUB_PASSWORD', "hub.docker.com-springbuildmaster")
                }
            }

            steps {

                if (fullProfile) {
                    shell(cleanAndInstall())
                }
                if (buildApps) {
                    shell(cleanAndDeployWithGenerateApps(project))
                }
                else if (!fullProfile) {
                    shell(cleanAndDeploy())
                }
//                maven {
//                    mavenInstallation(maven33())
//                    if (fullProfile) {
//                        goals('clean install -U -Pspring')
//                    }
//                    if (buildApps) {
//                        goals('clean deploy -U -Pspring -PgenerateApps -Pmilestone')
//                    }
//                    else if (!fullProfile) {
//                        goals('clean deploy -U -Pspring -Pmilestone')
//                    }
//                }
                if (buildApps) {
                    shell("""#!/bin/bash -x
					export MAVEN_PATH=${mavenBin()}
					${setupGitCredentials()}
					echo "Building apps"
                    cd apps
                    ../mvnw clean deploy
					${cleanGitCredentials()}
					""")
                }
                if (dockerHubPush) {
                    shell("""#!/bin/bash -x
					export MAVEN_PATH=${mavenBin()}
					${setupGitCredentials()}
					echo "Pushing to Docker Hub"
                    cd apps
                    set +x
                    ../mvnw -U --batch-mode clean package docker:build docker:push -DskipTests -Ddocker.username="\$${dockerHubUserNameEnvVar()}" -Ddocker.password="\$${dockerHubPasswordEnvVar()}"
					set -x

					git commit -am"Updating release version to 1.1.0.RC1"

                    ./mvnw versions:set -DnewVersion=1.1.0.BUILD-SNAPSHOT -DgenerateBackupPoms=false
                    ./mvnw versions:set -DnewVersion=1.1.0.BUILD-SNAPSHOT -DgenerateBackupPoms=false -pl :$project"-app-dependencies"
                    ./mvnw versions:update-parent -DparentVersion=1.1.0.BUILD-SNAPSHOT -Pspring -DgenerateBackupPoms=false
                    ./mvnw versions:update-parent -DparentVersion=1.1.0.BUILD-SNAPSHOT -Pspring -DgenerateBackupPoms=false -pl :$project"-app-dependencies"
                    git commit -am"Updating next version to 1.1.0.BUILD-SNAPSHOT"

                    git push origin master



					${cleanGitCredentials()}
					""")
                }
            }
            configure {

                if (fullProfile) {
                    artifactoryMavenBuild(it as Node) {
                        mavenVersion(maven33())
                        goals('clean install -U -Pfull -Pspring -Pmilestone')
                    }
                    artifactoryMaven3Configurator(it as Node)
                }

            }

            publishers {
                //mailer('scdf-ci@pivotal.io', true, true)
                if (checkTests) {
                    archiveJunit mavenJUnitResults()
                }
            }
        }
    }
}
