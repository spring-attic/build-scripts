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
    String cleanAndDeploy() {
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

    @Override
    String cleanAndDeployWithGenerateApps() {
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
