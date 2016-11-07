package io.springframework.scstappstarters.common

import groovy.transform.CompileStatic

/**
 * @author Soby Chacko
 */
@CompileStatic
class AllScstAppStarterJobs {

    public static final List<String> PHASE1_JOBS = ['log', 'time', 'ftp', 'sftp', 'file']

    public static final List<String> PHASE2_JOBS = ['jms', 'http', 'jdbc', 'twitter', 'rabbit']

    public static final List<String> PHASE3_JOBS = ['gemfire', 'cassandra', 'hdfs', 'websocket', 'router']

    public static final List<String> PHASE4_JOBS = ['counter', 'aggregate-counter', 'field-value-counter', 'bridge', 'gpfdist']

    public static final List<String> PHASE5_JOBS = ['tasklauncher-local', 'tasklauncher-yarn', 'tasklauncher-cloudfoundry', 'throughput', 'aws-s3']

    public static final List<String> PHASE6_JOBS = ['filter', 'groovy-filter', 'groovy-transform', 'httpclient']

    public static final List<String> PHASE7_JOBS = ['load-generator', 'loggregator', 'mail', 'mongodb', 'pmml']

    public static final List<String> PHASE8_JOBS = ['redis-pubsub', 'scriptable-transform', 'splitter', 'syslog', 'tcp']

    public static final List<String> PHASE9_JOBS = ['trigger', 'triggertask', 'tasklaunchrequest-transform', 'transform']

    public static final List<String> ALL_JOBS = PHASE1_JOBS + PHASE2_JOBS +
                                                PHASE3_JOBS + PHASE4_JOBS +
                                                PHASE5_JOBS + PHASE6_JOBS +
                                                PHASE7_JOBS + PHASE8_JOBS + PHASE9_JOBS;

    public static final List<List<String>> PHASES = [PHASE1_JOBS, PHASE2_JOBS,
                                                     PHASE3_JOBS, PHASE4_JOBS,
                                                     PHASE5_JOBS, PHASE6_JOBS,
                                                     PHASE7_JOBS, PHASE8_JOBS, PHASE9_JOBS]

}
