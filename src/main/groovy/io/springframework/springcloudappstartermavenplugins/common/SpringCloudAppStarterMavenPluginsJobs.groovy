package io.springframework.springcloudappstartermavenplugins.common

import io.springframework.common.job.BuildAndDeploy

/**
 * @author Soby Chacko
 */
trait SpringCloudAppStarterMavenPluginsJobs extends BuildAndDeploy {

    @Override
    String projectSuffix() {
        return 'spring-cloud-app-starter'
    }

    String cleanAndDeploy(String project) {
        if (project != null && !project.isEmpty()) {
            return """
                        cd "${project}"
                        ./mvnw clean deploy -U
                    """

        }
        else {
            return '''
                        ./mvnw clean deploy -U
                        '''
        }
    }

}
