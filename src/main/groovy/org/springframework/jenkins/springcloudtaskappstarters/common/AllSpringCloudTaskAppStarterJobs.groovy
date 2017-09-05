package org.springframework.jenkins.springcloudtaskappstarters.common

import groovy.transform.CompileStatic

/**
 * @author Soby Chacko
 */
@CompileStatic
class AllSpringCloudTaskAppStarterJobs {

    public static final List<String> PHASE1_JOBS = ['timestamp', 'spark-client', 'spark-cluster', 'spark-yarn', 'jdbchdfs-local', 'composed-task-runner', 'timestamp-batch']
    //Sqoop apps are not released and disabling the builds
    //public static final List<String> PHASE2_JOBS = ['sqoop-job']

    public static final List<String> ALL_JOBS = PHASE1_JOBS;

    public static final List<List<String>> PHASES = [PHASE1_JOBS]

}
