package io.springframework.springcloudtaskappstarters.common

import io.springframework.scstappstarters.common.SpringScstAppStarterJobs

/**
 * @author Soby Chacko
 */
trait SpringCloudTaskAppStarterJobs extends SpringScstAppStarterJobs {

    @Override
    String projectSuffix() {
        return 'spring-cloud-task-app-starters'
    }

    @Override
    String cleanAndDeploy(String releaseVersion) {
        return """
					#!/bin/bash -x
					./mvnw versions:set -DnewVersion=$releaseVersion -DgenerateBackupPoms=false
					./mvnw versions:set -DnewVersion=$releaseVersion -DgenerateBackupPoms=false -pl :task-app-starters-core-dependencies
			   		lines=\$(find . -type f -name pom.xml | xargs grep SNAPSHOT | wc -l)
					if [ \$lines -eq 0 ]; then
						./mvnw clean deploy -U -Pspring
					else
						echo "Snapshots found. Aborting the release build."
					fi
			   """

    }

    @Override
    String cleanAndDeployWithGenerateApps(String project, String releaseVersion, String parentVersion) {
        return """
					#!/bin/bash -x
					rm -rf apps
					./mvnw versions:set -DnewVersion=$releaseVersion -DgenerateBackupPoms=false
					./mvnw versions:set -DnewVersion=$releaseVersion -DgenerateBackupPoms=false -pl :$project"-task-app-dependencies"
					./mvnw versions:update-parent -DparentVersion=$parentVersion -Pspring -DgenerateBackupPoms=false
					lines=\$(find . -type f -name pom.xml | xargs grep SNAPSHOT | wc -l)
					if [ \$lines -eq 0 ]; then
						./mvnw clean deploy -U -Pspring -PgenerateApps
					else
						echo "Snapshots found. Aborting the release build."
					fi
			   """
    }
}
