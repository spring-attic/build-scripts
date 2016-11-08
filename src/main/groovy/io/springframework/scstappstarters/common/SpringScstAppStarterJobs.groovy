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

	@Override
	String cleanAndDeploy() {
		return """
					#!/bin/bash -x
					git checkout master
					git pull origin master
					./mvnw versions:set -DnewVersion=1.1.0.RC1 -DgenerateBackupPoms=false
					./mvnw versions:set -DnewVersion=1.1.0.RC1 -DgenerateBackupPoms=false -pl :app-starters-core-dependencies
			   		lines=\$(find . -type f -name pom.xml | xargs grep SNAPSHOT | wc -l)
					if [ \$lines -eq 0 ]; then
						./mvnw clean deploy -U -Pspring
						git commit -am"Updating release version to 1.1.0.RC1"
						./mvnw versions:set -DnewVersion=1.1.0.BUILD-SNAPSHOT -DgenerateBackupPoms=false
						./mvnw versions:set -DnewVersion=1.1.0.BUILD-SNAPSHOT -DgenerateBackupPoms=false -pl :app-starters-core-dependencies
						git commit -am"Updating next version to 1.1.0.BUILD-SNAPSHOT"
						git push origin master
					else
						echo "Snapshots found. Aborting the release build."
					fi
			   """

	}

	String cleanAndDeployWithGenerateApps(String project) {
		return """
					#!/bin/bash -x
					git checkout master
					git pull origin master
					./mvnw versions:set -DnewVersion=1.1.0.RC1 -DgenerateBackupPoms=false
					./mvnw versions:set -DnewVersion=1.1.0.RC1 -DgenerateBackupPoms=false -pl :$project"-app-dependencies"
					./mvnw versions:update-parent -DparentVersion=1.1.0.RC1 -Pspring -DgenerateBackupPoms=false
					./mvnw versions:update-parent -DparentVersion=1.1.0.RC1 -Pspring -DgenerateBackupPoms=false -pl :$project"-app-dependencies"
			   		lines=\$(find . -type f -name pom.xml | xargs grep SNAPSHOT | wc -l)
					if [ \$lines -eq 0 ]; then
						./mvnw clean deploy -U -Pspring -PgenerateApps
						git commit -am"Updating release version to 1.1.0.RC1"
						./mvnw versions:set -DnewVersion=1.1.0.BUILD-SNAPSHOT -DgenerateBackupPoms=false
						./mvnw versions:set -DnewVersion=1.1.0.BUILD-SNAPSHOT -DgenerateBackupPoms=false -pl :$project"-app-dependencies"
						./mvnw versions:update-parent -DparentVersion=1.1.0.BUILD-SNAPSHOT -Pspring -DgenerateBackupPoms=false
						./mvnw versions:update-parent -DparentVersion=1.1.0.BUILD-SNAPSHOT -Pspring -DgenerateBackupPoms=false -pl :$project"-app-dependencies"
						git commit -am"Updating next version to 1.1.0.BUILD-SNAPSHOT"
						git push origin master
					else
						echo "Snapshots found. Aborting the release build."
					fi
			   """
	}

	String cleanAndInstall() {
		return '''./mvnw clean install -U -Pspring'''
	}
}