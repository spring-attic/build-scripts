package io.springframework.scdataflowmetricscollector.ci

import io.springframework.common.job.Cron
import io.springframework.common.job.JdkConfig
import io.springframework.common.job.Maven
import io.springframework.common.job.TestPublisher
import io.springframework.scstappstarters.common.SpringScstAppStarterJobs
import javaposse.jobdsl.dsl.DslFactory

import static io.springframework.common.job.Artifactory.artifactoryMaven3Configurator
import static io.springframework.common.job.Artifactory.artifactoryMavenBuild

/**
 * @author Soby Chacko
 */
class SpringCloudDataFlowMetricsCollectorBuildMaker implements JdkConfig, TestPublisher,
        Cron, SpringScstAppStarterJobs, Maven {

    private final DslFactory dsl
    final String organization
    final String project

    final String branchToBuild = "master"

    SpringCloudDataFlowMetricsCollectorBuildMaker(DslFactory dsl, String organization,
                                                  String project) {
        this.dsl = dsl
        this.organization = organization
        this.project = project
    }

    @Override
    String projectSuffix() {
        return ''
    }

    static String cleanAndDeployMileStone() {
        return """
					#!/bin/bash -x

			   		lines=\$(find . -type f -name pom.xml | xargs grep SNAPSHOT | wc -l)
					if [ \$lines -eq 0 ]; then
						./mvnw clean deploy -U -Pspring -PgenerateApps
					else
						echo "Snapshots found. Aborting the release build."
					fi
			   """

    }

    static String cleanAndDeployGA() {
        return """
					#!/bin/bash -x

			   		lines=\$(find . -type f -name pom.xml | xargs egrep "SNAPSHOT|M[0-9]|RC[0-9]" | wc -l)
					if [ \$lines -eq 0 ]; then
						./mvnw clean deploy -U -Pspring -PgenerateApps
					else
						echo "Snapshots or RC/milestones found. Aborting the release build."
					fi
			   """
    }

    static String cleanAndDeploySnapshots() {
        return """
					#!/bin/bash -x

			   		./mvnw clean deploy -U -Pspring -PgenerateApps
			   """
    }

    void deploy(boolean checkTests = true, boolean githubPushTrigger = true, boolean isMilestoneOrRcRelease = false,
                boolean isGARelease = false) {
        dsl.job("${prefixJob(project)}-${branchToBuild}-ci") {
            if (githubPushTrigger) {
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
            }

            steps {
                if (isMilestoneOrRcRelease) {
                    shell(cleanAndDeployMileStone())
                }
                else if (isGARelease) {
                    shell(cleanAndDeployGA())
                }
                else {
                    shell(cleanAndDeploySnapshots())
                }

                shell("""set -e
                    #!/bin/bash -x
					export MAVEN_PATH=${mavenBin()}
					${setupGitCredentials()}
					echo "Building apps"
                    cd apps
                    ../mvnw clean deploy -U
					${cleanGitCredentials()}
					""")

                shell("""set -e
                    #!/bin/bash -x
					export MAVEN_PATH=${mavenBin()}
					${setupGitCredentials()}
					echo "Pushing to Docker Hub"
                    cd apps
                    set +x
                    ../mvnw -U --batch-mode clean package docker:build docker:push -DskipTests -Ddocker.username="\$${dockerHubUserNameEnvVar()}" -Ddocker.password="\$${dockerHubPasswordEnvVar()}"
					set -x

					${cleanGitCredentials()}
					""")
            }
            configure {
                artifactoryMavenBuild(it as Node) {
                    mavenVersion(maven33())
                    if (isMilestoneOrRcRelease) {
                        goals('clean install -U -Pfull -Pspring -Pmilestone')
                    }
                    else {
                        goals('clean install -U -Pfull -Pspring')
                    }
                }
                artifactoryMaven3Configurator(it as Node){
                    if (isMilestoneOrRcRelease) {
                        deployReleaseRepository("libs-milestone-local")
                    }
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
