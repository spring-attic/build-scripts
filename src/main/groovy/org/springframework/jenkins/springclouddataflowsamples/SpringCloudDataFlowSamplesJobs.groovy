package org.springframework.jenkins.springclouddataflowsamples

import org.springframework.jenkins.common.job.BuildAndDeploy

/**
 * @author Soby Chacko
 */
trait SpringCloudDataFlowSamplesJobs extends BuildAndDeploy {

    @Override
    String projectSuffix() {
        return 'spring-cloud-dataflow-samples'
    }

}