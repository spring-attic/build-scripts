package io.springframework.scstappstarters.common

import groovy.transform.CompileStatic

/**
 * @author Soby Chacko
 */
@CompileStatic
class AllScstAppStarterJobs {

    public static final List<String> PHASE1_JOBS = ['log', 'time', 'ftp', 'sftp', 'file']

    public static final List<String> PHASE2_JOBS = ['jms', 'http', 'jdbc', 'twitter', 'rabbit']

    public static final List<String> PHASE3_JOBS = ['gemfire', 'cassandra', 'hdfs', 'websocket']

    public static final List<String> PHASE4_JOBS = ['counter', 'aggregate-counter', 'field-value-counter']

    public static final List<String> ALL_JOBS = PHASE1_JOBS + PHASE2_JOBS + PHASE3_JOBS + PHASE4_JOBS;

    public static final List<List<String>> PHASES = [PHASE1_JOBS, PHASE2_JOBS, PHASE3_JOBS, PHASE4_JOBS]

}
