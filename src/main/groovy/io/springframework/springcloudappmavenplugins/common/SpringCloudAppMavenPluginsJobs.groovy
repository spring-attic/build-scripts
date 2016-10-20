package io.springframework.springcloudappmavenplugins.common

import io.springframework.common.BuildAndDeploy

/**
 * @author Soby Chacko
 */
trait SpringCloudAppMavenPluginsJobs extends BuildAndDeploy {

    @Override
    String projectSuffix() {
        return 'spring-cloud-app-maven-plugin'
    }

}
