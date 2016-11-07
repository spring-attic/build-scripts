package io.springframework.springcloudappstartermavenplugins.common

import io.springframework.common.BuildAndDeploy

/**
 * @author Soby Chacko
 */
trait SpringCloudAppStarterMavenPluginsJobs extends BuildAndDeploy {

    @Override
    String projectSuffix() {
        return 'spring-cloud-app-starter'
    }

    @Override
    String cleanAndDeploy() {
        return '''./mvnw clean deploy -U'''
    }

}
