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

    String cleanAndDeploy(String project, boolean isGaRelease) {
        if (project != null && !project.isEmpty()) {
            return isGaRelease ?
                    """
                        cd "${project}"
                        rm -rf target
                        
                        lines=\$(find . -type f -name pom.xml | xargs egrep "SNAPSHOT|M[0-9]|RC[0-9]" | wc -l)
                        if [ \$lines -eq 0 ]; then
                            ./mvnw clean deploy -Pcentral -U
                        else
                            echo "Non release versions found. Aborting build"
                        fi
                    """ :
                    """
                        cd "${project}"
                        ./mvnw clean deploy -U
                    """

        }
        else {
            return isGaRelease ?
                    '''
                        rm -rf target
                        lines=\$(find . -type f -name pom.xml | xargs egrep "SNAPSHOT|M[0-9]|RC[0-9]" | wc -l)
                        if [ \$lines -eq 0 ]; then
                            ./mvnw clean deploy -Pcentral -U
                        else
                            echo "Non release versions found. Aborting build"
                        fi
                    ''' :
                    '''
                        ./mvnw clean deploy -U
                    '''
        }
    }

}
