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
}
