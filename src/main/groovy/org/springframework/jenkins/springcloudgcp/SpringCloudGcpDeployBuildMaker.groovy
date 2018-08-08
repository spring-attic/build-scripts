package org.springframework.jenkins.springcloudgcp

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.TestPublisher

import static org.springframework.jenkins.common.job.Artifactory.artifactoryMaven3Configurator
import static org.springframework.jenkins.common.job.Artifactory.artifactoryMavenBuild

/**
 * @author Marcin Grzejszczak
 */
class SpringCloudGcpDeployBuildMaker implements JdkConfig, TestPublisher,
        Cron, SpringCloudGcpJobs, Maven {

    private final DslFactory dsl
    final String organization

    SpringCloudGcpDeployBuildMaker(DslFactory dsl) {
        this.dsl = dsl
        this.organization = 'spring-cloud'
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

    String cleanAndDeploy(boolean isRelease) {
        return isRelease ? """
					#!/bin/bash -x

			   		lines=\$(find . -path ./spring-cloud-gcp-samples -prune -o -type f -name pom.xml | xargs grep SNAPSHOT | grep -v ".contains(" | grep -v regex | wc -l)
					if [ \$lines -eq 0 ]; then
						./mvnw clean deploy -U -Pspring
					else
						echo "Snapshots found. Aborting the release build."
					fi
			   """
                    :
                """
                    ./mvnw clean deploy -U -Pspring
                """

    }

    void deploy(boolean isRelease = false, String releaseType = "", String branchToBuild = "master") {
        String project = 'spring-cloud-gcp'
        dsl.job("${prefixJob(project)}-$branchToBuild-ci") {
            triggers {
                if (!isRelease){
                    githubPush()
                }
            }
            jdk jdk8()
            wrappers {
                colorizeOutput()
                maskPasswords()

                if (releaseType.equals("ga")) {
                    credentialsBinding {
                        file('FOO_SEC', "spring-signing-secring.gpg")
                        file('FOO_PUB', "spring-signing-pubring.gpg")
                        string('FOO_PASSPHRASE', "spring-gpg-passphrase")
                        usernamePassword('SONATYPE_USER', 'SONATYPE_PASSWORD', "oss-token")
                        usernamePassword('DOCKER_HUB_USERNAME', 'DOCKER_HUB_PASSWORD', "hub.docker.com-springbuildmaster")
                    }
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
            steps {
                if (isRelease && releaseType.equals("ga")){
                    shell(cleanAndDeployGA())
                }
                else {
                    shell(cleanAndDeploy(isRelease))
                }
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
