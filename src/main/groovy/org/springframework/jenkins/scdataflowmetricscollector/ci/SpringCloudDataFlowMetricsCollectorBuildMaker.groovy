package org.springframework.jenkins.scdataflowmetricscollector.ci

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.TestPublisher
import org.springframework.jenkins.scstappstarters.common.SpringScstAppStarterJobs
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

    String cleanAndDeployMileStone() {
        return """
					#!/bin/bash -x
					rm -rf apps

			   		lines=\$(find . -type f -name pom.xml | xargs grep SNAPSHOT | grep -v regex | wc -l)
					if [ \$lines -eq 0 ]; then
						./mvnw clean deploy -U -Pspring -PgenerateApps
					else
						echo "Snapshots found. Aborting the release build."
					fi
			   """

    }

    String cleanAndDeployGA() {
        return """
                    #!/bin/bash -x
                    rm -rf apps

                    lines=\$(find . -type f -name pom.xml | xargs egrep "SNAPSHOT|M[0-9]|RC[0-9]" | grep -v regex | wc -l)
                    if [ \$lines -eq 0 ]; then
                        set +x
                        ./mvnw clean deploy -Pspring -PgenerateApps -Dgpg.secretKeyring="\$${gpgSecRing()}" -Dgpg.publicKeyring="\$${
            gpgPubRing()}" -Dgpg.passphrase="\$${gpgPassphrase()}" -DSONATYPE_USER="\$${sonatypeUser()}" -DSONATYPE_PASSWORD="\$${sonatypePassword()}" -Pcentral -U
                        set -x
                    else
                        echo "Non release versions found. Aborting build"
                    fi
                """
    }

    String cleanAndDeploySnapshots() {
        return """
					#!/bin/bash -x
					rm -rf apps

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
                if (isGARelease) {
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
                if (isMilestoneOrRcRelease) {
                    shell(cleanAndDeployMileStone())
                }
                else if (isGARelease) {
                    shell(cleanAndDeployGA())
                }
                else {
                    shell(cleanAndDeploySnapshots())
                }

                if (isGARelease) {
                    shell("""set -e
                        #!/bin/bash -x
                        export MAVEN_PATH=${mavenBin()}
                        ${setupGitCredentials()}
                        echo "Building apps"
                        cd apps
                        set +x
                        ../mvnw clean deploy -Pspring -Dgpg.secretKeyring="\$${gpgSecRing()}" -Dgpg.publicKeyring="\$${
                        gpgPubRing()}" -Dgpg.passphrase="\$${gpgPassphrase()}" -DSONATYPE_USER="\$${sonatypeUser()}" -DSONATYPE_PASSWORD="\$${sonatypePassword()}" -Pcentral -U
                        set -x
                        ${cleanGitCredentials()}
                        """)
                }
                else {
                    shell("""set -e
                        #!/bin/bash -x
                        export MAVEN_PATH=${mavenBin()}
                        ${setupGitCredentials()}
                        echo "Building apps"
                        cd apps
                        ../mvnw clean deploy -U
                        ${cleanGitCredentials()}
                        """)
                }

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
//                artifactoryMavenBuild(it as Node) {
//                    mavenVersion(maven35())
//                    goals('clean install -U -Pfull -Pspring -pl :spring-cloud-dataflow-collector-metrics-docs')
//                }
//                artifactoryMaven3Configurator(it as Node) {
//                    if (isMilestoneOrRcRelease) {
//                        deployReleaseRepository("libs-milestone-local")
//                    }
//                    else if (isGARelease) {
//                        deployReleaseRepository("libs-release-local")
//                    }
//                }
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
