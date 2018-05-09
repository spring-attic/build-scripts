package org.springframework.jenkins.scstappstarters.common

import org.springframework.jenkins.common.job.BuildAndDeploy

/**
 * @author Marcin Grzejszczak
 */
trait SpringScstAppStarterJobs extends BuildAndDeploy {

	@Override
	String projectSuffix() {
		return 'spring-scst-app-starters'
	}

	/**
	 * Dirty hack cause Jenkins is not inserting Maven to path...
	 * Requires using Maven3 installation before calling
	 *
	 */
	String mavenBin() {
		return "/opt/jenkins/data/tools/hudson.tasks.Maven_MavenInstallation/maven33/apache-maven-3.3.9/bin/"
	}

	String setupGitCredentials() {
		return """
					set +x
					git config user.name "${githubUserName()}"
					git config user.email "${githubEmail()}"
					git config credential.helper "store --file=/tmp/gitcredentials"
					echo "https://\$${githubRepoUserNameEnvVar()}:\$${githubRepoPasswordEnvVar()}@github.com" > /tmp/gitcredentials
					set -x
				"""
	}

	String githubUserName() {
		return 'spring-buildmaster'
	}

	String githubEmail() {
		return 'buildmaster@springframework.org'
	}

	String githubRepoUserNameEnvVar() {
		return 'GITHUB_REPO_USERNAME'
	}

	String githubRepoPasswordEnvVar() {
		return 'GITHUB_REPO_PASSWORD'
	}

	String dockerHubUserNameEnvVar() {
		return 'DOCKER_HUB_USERNAME'
	}

	String dockerHubPasswordEnvVar() {
		return 'DOCKER_HUB_PASSWORD'
	}

	String cleanGitCredentials() {
		return "rm -rf /tmp/gitcredentials"
	}

	String cleanAndDeploy(boolean isRelease, String releaseType) {
		if (isRelease && releaseType != null && !releaseType.equals("milestone")) {

			return  """
                        lines=\$(find . -type f -name pom.xml | xargs egrep "SNAPSHOT|M[0-9]|RC[0-9]" | grep -v regex | wc -l)
                        if [ \$lines -eq 0 ]; then
                            set +x
                            ./mvnw clean deploy -Pspring -Dgpg.secretKeyring="\$${gpgSecRing()}" -Dgpg.publicKeyring="\$${
				gpgPubRing()}" -Dgpg.passphrase="\$${gpgPassphrase()}" -DSONATYPE_USER="\$${sonatypeUser()}" -DSONATYPE_PASSWORD="\$${sonatypePassword()}" -Pcentral -U
                            set -x
                        else
                            echo "Non release versions found. Aborting build"
                        fi
                    """
		}
		if (isRelease && releaseType != null && releaseType.equals("milestone")) {
			return """
					#!/bin/bash -x

			   		lines=\$(find . -type f -name pom.xml | xargs grep SNAPSHOT | wc -l)
					if [ \$lines -eq 0 ]; then
						./mvnw clean deploy -U -Pspring
					else
						echo "Snapshots found. Aborting the release build."
					fi
			   """
		}
	}

	String cleanAndDeployWithGenerateApps(boolean isRelease, String releaseType) {
		if (isRelease && releaseType != null && !releaseType.equals("milestone")) {
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
        if (isRelease && releaseType != null && releaseType.equals("milestone")) {
            return """
                #!/bin/bash -x
                rm -rf apps

                lines=\$(find . -type f -name pom.xml | xargs grep SNAPSHOT | wc -l)
                if [ \$lines -eq 0 ]; then
                    ./mvnw clean deploy -U -Pspring -PgenerateApps
                else
                    echo "Snapshots found. Aborting the release build."
                fi
           """
        }
	}

	String removeAppsDirectory() {
		return """
					#!/bin/bash -x
					rm -rf apps
			   """
	}

	String cleanAndInstall(boolean isRelease, String releaseType) {
        if (isRelease && releaseType != null && !releaseType.equals("milestone")) {
            return """
                    #!/bin/bash -x
                    rm -rf apps

                    lines=\$(find . -type f -name pom.xml | xargs egrep "SNAPSHOT|M[0-9]|RC[0-9]" | grep -v ".contains(" | grep -v regex | wc -l)
                    if [ \$lines -eq 0 ]; then
                        set +x
                        ./mvnw clean deploy -Pspring -Dgpg.secretKeyring="\$${gpgSecRing()}" -Dgpg.publicKeyring="\$${
                gpgPubRing()}" -Dgpg.passphrase="\$${gpgPassphrase()}" -DSONATYPE_USER="\$${sonatypeUser()}" -DSONATYPE_PASSWORD="\$${sonatypePassword()}" -Pcentral -U
                        set -x
                    else
                        echo "Non release versions found. Aborting build"
                    fi
                """
        }
        if (isRelease && releaseType != null && releaseType.equals("milestone")) {
            return """
					#!/bin/bash -x

			   		lines=\$(find . -type f -name pom.xml | xargs grep SNAPSHOT | grep -v ".contains(" | grep -v regex | wc -l)
					if [ \$lines -eq 0 ]; then
						./mvnw clean install -U -Pspring
					else
						echo "Snapshots found. Aborting the release build."
					fi
			   """
        }
	}

	String gpgSecRing() {
		return 'FOO_SEC'
	}

	String gpgPubRing() {
		return 'FOO_PUB'
	}

	String gpgPassphrase() {
		return 'FOO_PASSPHRASE'
	}

	String sonatypeUser() {
		return 'SONATYPE_USER'
	}

	String sonatypePassword() {
		return 'SONATYPE_PASSWORD'
	}
}