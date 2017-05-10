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

    public static final List<String> PHASE6_JOBS = ['filter', 'groovy-filter', 'groovy-transform', 'httpclient', 'aggregator']

    public static final List<String> PHASE7_JOBS = ['load-generator', 'loggregator', 'mail', 'mongodb', 'pmml']

    public static final List<String> PHASE8_JOBS = ['redis-pubsub', 'scriptable-transform', 'splitter', 'syslog', 'tcp']

    public static final List<String> PHASE9_JOBS = ['trigger', 'triggertask', 'tasklaunchrequest-transform', 'transform', 'header-enricher']

    public static final List<String> PHASE10_JOBS = ['tensorflow']

    public static final List<String> ALL_JOBS =
                                                PHASE1_JOBS + PHASE2_JOBS +
                                                PHASE3_JOBS + PHASE4_JOBS +
                                                PHASE5_JOBS + PHASE6_JOBS +
                                                PHASE7_JOBS + PHASE8_JOBS + PHASE9_JOBS + PHASE10_JOBS;

    public static final List<List<String>> PHASES = [
                                                    PHASE1_JOBS, PHASE2_JOBS,
                                                     PHASE3_JOBS, PHASE4_JOBS,
                                                     PHASE5_JOBS, PHASE6_JOBS,
                                                     PHASE7_JOBS, PHASE8_JOBS,
                                                     PHASE9_JOBS, PHASE10_JOBS]

    public static final Map<String, String> RELEASE_PHASE1_JOBS = ['log':'1.2.0.M1', 'time':'1.2.0.M1', 'ftp':'1.2.0.M1', 'sftp':'1.2.0.M1', 'file':'1.2.0.M1']

    public static final Map<String, String> RELEASE_PHASE2_JOBS = ['jms':'1.2.0.M1', 'http':'1.1.3.M1', 'jdbc':'1.2.0.M1', 'twitter':'1.2.0.M1', 'rabbit':'1.2.0.M1']

    public static final Map<String, String> RELEASE_PHASE3_JOBS = ['gemfire':'1.2.0.M1', 'cassandra':'1.2.0.M1', 'hdfs':'1.2.0.M1', 'websocket':'1.2.0.M1', 'router':'1.2.0.M1']

    public static final Map<String, String> RELEASE_PHASE4_JOBS = ['counter':'1.2.0.M1', 'aggregate-counter':'1.2.0.M1', 'field-value-counter':'1.2.0.M1', 'bridge':'1.2.0.M1', 'gpfdist':'1.2.0.M1']

    public static final Map<String, String> RELEASE_PHASE5_JOBS = ['tasklauncher-local':'1.2.0.M1', 'tasklauncher-yarn':'1.2.0.M1', 'tasklauncher-cloudfoundry':'1.2.0.M1', 'throughput':'1.2.0.M1', 'aws-s3':'1.2.0.M1']

    public static final Map<String, String> RELEASE_PHASE6_JOBS = ['filter':'1.2.0.M1', 'groovy-filter':'1.2.0.M1', 'groovy-transform':'1.2.0.M1', 'httpclient':'1.2.0.M1', 'aggregator':'1.0.0.M1']

    public static final Map<String, String> RELEASE_PHASE7_JOBS = ['load-generator':'1.2.0.M1', 'loggregator':'1.2.0.M1', 'mail':'1.2.0.M1', 'mongodb':'1.2.0.M1', 'pmml':'1.2.0.M1']

    public static final Map<String, String> RELEASE_PHASE8_JOBS = ['redis-pubsub':'1.2.0.M1', 'scriptable-transform':'1.2.0.M1', 'splitter':'1.2.0.M1', 'syslog':'1.2.0.M1', 'tcp':'1.2.0.M1']

    public static final Map<String, String> RELEASE_PHASE9_JOBS = ['trigger':'1.2.0.M1', 'triggertask':'1.2.0.M1', 'tasklaunchrequest-transform':'1.2.0.M1', 'transform':'1.2.0.M1', 'header-enricher':'1.0.0.M1']

    public static final Map<String, String> RELEASE_ALL_JOBS =
            RELEASE_PHASE1_JOBS + RELEASE_PHASE2_JOBS +
            RELEASE_PHASE3_JOBS + RELEASE_PHASE4_JOBS +
            RELEASE_PHASE5_JOBS + RELEASE_PHASE6_JOBS +
            RELEASE_PHASE7_JOBS + RELEASE_PHASE8_JOBS + RELEASE_PHASE9_JOBS;

}
