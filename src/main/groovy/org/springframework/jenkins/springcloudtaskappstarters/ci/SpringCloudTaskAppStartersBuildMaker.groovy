package org.springframework.jenkins.springcloudtaskappstarters.ci

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.TestPublisher
import org.springframework.jenkins.springcloudtaskappstarters.common.SpringCloudTaskAppStarterJobs

import static org.springframework.jenkins.common.job.Artifactory.artifactoryMaven3Configurator
import static org.springframework.jenkins.common.job.Artifactory.artifactoryMavenBuild

/**
 * @author Marcin Grzejszczak
 */
class SpringCloudTaskAppStartersBuildMaker implements JdkConfig, TestPublisher,
        Cron, SpringCloudTaskAppStarterJobs, Maven {

    private final DslFactory dsl
    final String organization
    final String project

    final String branchToBuild = "master"

    SpringCloudTaskAppStartersBuildMaker(DslFactory dsl, String organization,
                                         String project, String branchToBuild = "master") {
        this.dsl = dsl
        this.organization = organization
        this.project = project
        this.branchToBuild = branchToBuild
    }

    void deploy(boolean appsBuild = true, boolean checkTests = true,
                boolean dockerHubPush = true, boolean githubPushTrigger = true,
                boolean docsBuild = false, boolean isRelease = false,
                String releaseType = "") {
        dsl.job("${prefixJob(project)}-${branchToBuild}-ci") {
            if (githubPushTrigger && !isRelease) {
                triggers {
                    githubPush()
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

            jdk jdk8()
            wrappers {
                colorizeOutput()
                maskPasswords()
                credentialsBinding {
                    usernamePassword('DOCKER_HUB_USERNAME', 'DOCKER_HUB_PASSWORD', "hub.docker.com-springbuildmaster")
                }
                if (isRelease && releaseType != null && !releaseType.equals("milestone")) {
                    credentialsBinding {
                        file('FOO_SEC', "spring-signing-secring.gpg")
                        file('FOO_PUB', "spring-signing-pubring.gpg")
                        string('FOO_PASSPHRASE', "spring-gpg-passphrase")
                        usernamePassword('SONATYPE_USER', 'SONATYPE_PASSWORD', "oss-token")
                        usernamePassword('DOCKER_HUB_USERNAME', 'DOCKER_HUB_PASSWORD', "hub.docker.com-springbuildmaster")
                    }
                }
            }

            steps {
                if (appsBuild) {
                    shell(removeAppsDirectory())
                }
                if (isRelease) {
                    if (docsBuild) {
                        shell(cleanAndInstall(isRelease, releaseType))
                    }
                    else if (appsBuild) {
                        shell(cleanAndDeployWithGenerateApps(isRelease, releaseType))
                    }
                    else {
                        shell(cleanAndDeploy(isRelease, releaseType))
                    }
                }
                else {
                    maven {
                        mavenInstallation(maven35())
                        if (docsBuild) {
                            goals('clean install -U -Pspring')
                        }
                        else if (appsBuild) {
                            goals('clean deploy -U -Pspring -PgenerateApps')
                        }
                        else {
                            goals('clean deploy -U -Pspring')
                        }
                    }
                }
                String appDir = project.equals("composed-task-runner") ? "composedtaskrunner-task" : project + "-task"
                if (appsBuild) {
                    if (isRelease && releaseType != null) {
                        if (!releaseType.equals("milestone")) {
                            shell("""
                            #!/bin/bash -x
                            export MAVEN_PATH=${mavenBin()}
                            ${setupGitCredentials()}
                            echo "Building apps"
                            cd apps
                            cd ${appDir}
                            set +x
                            ./mvnw clean deploy -Dgpg.secretKeyring="\$${gpgSecRing()}" -Dgpg.publicKeyring="\$${
                                    gpgPubRing()}" -Dgpg.passphrase="\$${gpgPassphrase()}" -DSONATYPE_USER="\$${sonatypeUser()}" -DSONATYPE_PASSWORD="\$${sonatypePassword()}" -Pcentral -U
                            set -x
                            ${cleanGitCredentials()}
                            """)
                        }
                        else {
                            shell("""#!/bin/bash -x
                            export MAVEN_PATH=${mavenBin()}
                            ${setupGitCredentials()}
                            echo "Building apps"
                            cd apps
                            cd ${appDir}
                            ./mvnw clean deploy -Pmilestone
                            ${cleanGitCredentials()}
                            """)
                        }
                    }
                    else {
                        shell("""#!/bin/bash -x
                        export MAVEN_PATH=${mavenBin()}
                        ${setupGitCredentials()}
                        echo "Building apps"
                        cd apps
                        cd ${appDir}
                        ./mvnw clean deploy -U
                        ${cleanGitCredentials()}
                        """)
                    }
                }
                if (dockerHubPush) {
                    shell("""#!/bin/bash -x
					export MAVEN_PATH=${mavenBin()}
					${setupGitCredentials()}
					echo "Pushing to Docker Hub"
                    cd apps
                    cd ${appDir}
                    set +x
                    ./mvnw -U --batch-mode clean package docker:build docker:push -DskipTests -Ddocker.username="\$${dockerHubUserNameEnvVar()}" -Ddocker.password="\$${dockerHubPasswordEnvVar()}"
					set -x

					${cleanGitCredentials()}
					""")
                }
            }
            configure {

                if (docsBuild) {
//                    artifactoryMavenBuild(it as Node) {
//                        mavenVersion(maven33())
//                        if (releaseType != null && releaseType.equals("milestone")) {
//                            goals('clean install -U -Pfull -Pspring -Pmilestone')
//                        }
//                        else {
//                            goals('clean install -U -Pfull -Pspring')
//                        }
//                    }
                    artifactoryMavenBuild(it as Node) {
//                        mavenVersion(maven33())
//                        if (releaseType != null && releaseType.equals("milestone")) {
//                            goals('clean install -U -Pfull -Pspring -Pmilestone')
//                        }
//                        else {
//                            goals('clean install -U -Pfull -Pspring')
//                        }
                        mavenVersion(maven35())
                        goals('clean install -U -Pfull -Pspring')
                    }
                    artifactoryMaven3Configurator(it as Node) {
                        if (isRelease && releaseType != null && releaseType.equals("milestone")) {
                            deployReleaseRepository("libs-milestone-local")
                        }
                        else if (isRelease) {
                            deployReleaseRepository("libs-release-local")
                        }
                    }
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
