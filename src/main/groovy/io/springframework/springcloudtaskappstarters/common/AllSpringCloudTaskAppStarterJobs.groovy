package io.springframework.springcloudtaskappstarters.common

import groovy.transform.CompileStatic

/**
 * @author Soby Chacko
 */
@CompileStatic
class AllSpringCloudTaskAppStarterJobs {

    public static final List<String> PHASE1_JOBS = ['timestamp', 'spark-client', 'spark-cluster', 'spark-yarn', 'composed-task-runner']
    //TODO: sqoop-tool is disabled due to test failures
    public static final List<String> PHASE2_JOBS = ['sqoop-job', 'jdbchdfs-local']

    public static final List<String> ALL_JOBS = PHASE1_JOBS + PHASE2_JOBS;

    public static final List<List<String>> PHASES = [PHASE1_JOBS,
                                                     PHASE2_JOBS]

    public static final Map<String, String> RELEASE_PHASE1_JOBS = ['timestamp':'1.2.0.M1', 'spark-client':'1.2.0.M1', 'spark-cluster':'1.2.0.M1', 'spark-yarn':'1.2.0.M1', 'composed-task-runner':'1.2.0.M1']
    //TODO: Add sqoop-tool back
    public static final Map<String, String> RELEASE_PHASE2_JOBS = ['sqoop-job':'1.2.0.M1', 'jdbchdfs-local':'1.2.0.M1']


    public static final Map<String, String> RELEASE_ALL_JOBS =
            RELEASE_PHASE1_JOBS + RELEASE_PHASE2_JOBS;

}
