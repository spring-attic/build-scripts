package io.springframework.springcloudtaskappstarters.common

import groovy.transform.CompileStatic

/**
 * @author Soby Chacko
 */
@CompileStatic
class AllSpringCloudTaskAppStarterJobs {

    public static final List<String> PHASE1_JOBS = ['timestamp', 'spark-client', 'spark-cluster', 'spark-yarn']

    public static final List<String> PHASE2_JOBS = ['sqoop-job', 'sqoop-tool', 'jdbchdfs-local', 'composed-task-runner']

    public static final List<String> ALL_JOBS = PHASE1_JOBS + PHASE2_JOBS;

    public static final List<List<String>> PHASES = [PHASE1_JOBS,
                                                     PHASE2_JOBS]

}
