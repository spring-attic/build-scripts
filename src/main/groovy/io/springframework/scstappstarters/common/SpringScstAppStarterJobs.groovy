package io.springframework.scstappstarters.common

import io.springframework.common.BuildAndDeploy

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

	String cleanAndDeploy(String releaseVersion) {
		return """
					#!/bin/bash -x
					./mvnw versions:set -DnewVersion=$releaseVersion -DgenerateBackupPoms=false
					./mvnw versions:set -DnewVersion=$releaseVersion -DgenerateBackupPoms=false -pl :app-starters-core-dependencies
			   		lines=\$(find . -type f -name pom.xml | xargs grep SNAPSHOT | wc -l)
					if [ \$lines -eq 0 ]; then
						./mvnw clean deploy -U -Pspring
					else
						echo "Snapshots found. Aborting the release build."
					fi
			   """

	}

	String cleanAndDeployWithGenerateApps(String project, String releaseVersion, String parentVersion) {
		return """
					#!/bin/bash -x
					rm -rf apps
					./mvnw versions:set -DnewVersion=$releaseVersion -DgenerateBackupPoms=false
					./mvnw versions:set -DnewVersion=$releaseVersion -DgenerateBackupPoms=false -pl :$project"-app-dependencies"
					./mvnw versions:update-parent -DparentVersion=$parentVersion -Pspring -DgenerateBackupPoms=false -pl \\!$project-app-dependencies
					lines=\$(find . -type f -name pom.xml | xargs grep SNAPSHOT | wc -l)
					if [ \$lines -eq 0 ]; then
						./mvnw clean deploy -U -Pspring -PgenerateApps
					else
						echo "Snapshots found. Aborting the release build."
					fi
			   """
	}

	String cleanAndInstall(String releaseTrainVersion, String parentVersion) {

		return """
					#!/bin/bash -x
					./mvnw versions:set -DnewVersion=$releaseTrainVersion -DgenerateBackupPoms=false
					./mvnw versions:update-parent -DparentVersion=$parentVersion -Pspring -DgenerateBackupPoms=false

			   		lines=\$(find . -type f -name pom.xml | xargs grep SNAPSHOT | wc -l)
					if [ \$lines -eq 0 ]; then
						./mvnw clean install -U -Pspring
					else
						echo "Snapshots found. Aborting the release build."
					fi
			   """
	}
}