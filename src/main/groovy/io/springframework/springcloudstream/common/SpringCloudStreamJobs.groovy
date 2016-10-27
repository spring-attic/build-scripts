package io.springframework.springcloudstream.common

import io.springframework.common.BuildAndDeploy

/**
 * @author Marcin Grzejszczak
 */
trait SpringCloudStreamJobs extends BuildAndDeploy {

    @Override
    String projectSuffix() {
        return 'spring-cloud-stream'
    }

    String scriptToExecute(String script) {
        return """
						echo "Running script"
						bash ${script}
					"""
    }
}