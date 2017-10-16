package org.springframework.jenkins.scstappstarters.common

import groovy.transform.CompileStatic

/**
 * @author Soby Chacko
 */
@CompileStatic
class AllScstAppStarterJobs {

    public static final List<String> PHASE1_JOBS = ['log', 'time', 'throughput', 'bridge', 'groovy-transform', 'tasklauncher-local', 'header-enricher', 'function']

    public static final List<String> PHASE2_JOBS = ['router', 'trigger', 'loggregator', 'load-generator', 'mail', 'field-value-counter', 'pmml', 'tasklaunchrequest-transform']

    public static final List<String> PHASE3_JOBS = ['triggertask', 'websocket', 'http', 'syslog', 'filter', 'gpfdist', 'splitter', 'jms']

    public static final List<String> PHASE4_JOBS = ['groovy-filter', 'redis-pubsub', 'aggregate-counter', 'tasklauncher-yarn', 'tasklauncher-cloudfoundry', 'httpclient', 'twitter', 'counter']

    public static final List<String> PHASE5_JOBS = ['cassandra', 'aggregator', 'mqtt', 'rabbit', 'scriptable-transform', 'ftp', 'file', 'aws-s3']

    public static final List<String> PHASE6_JOBS = ['sftp', 'tensorflow', 'mongodb', 'hdfs', 'jdbc', 'gemfire', 'tcp', 'transform', 'python']

    public static final List<String> ALL_JOBS =
            PHASE1_JOBS + PHASE2_JOBS +
                    PHASE3_JOBS + PHASE4_JOBS +
                    PHASE5_JOBS + PHASE6_JOBS

    public static final List<List<String>> PHASES = [
            PHASE1_JOBS, PHASE2_JOBS,
            PHASE3_JOBS, PHASE4_JOBS,
            PHASE5_JOBS, PHASE6_JOBS]
    

    public static final Map<String, String> RELEASE_PHASE1_JOBS = ['log':'1.3.0.M1', 'time':'1.3.0.M1', 'throughput':'1.3.0.M1', 'bridge':'1.3.0.M1', 'groovy-transform':'1.3.0.M1', 'tasklauncher-local':'1.3.0.M1', 'transform':'1.3.0.M1', 'header-enricher':'1.3.0.M1']

    public static final Map<String, String> RELEASE_PHASE2_JOBS = ['router':'1.3.0.M1', 'trigger':'1.1.3.M1', 'loggregator':'1.3.0.M1', 'load-generator':'1.3.0.M1', 'mail':'1.3.0.M1', 'field-value-counter':'1.3.0.M1', 'pmml':'1.3.0.M1', 'tasklaunchrequest-transform':'1.3.0.M1']

    public static final Map<String, String> RELEASE_PHASE3_JOBS = ['triggertask':'1.3.0.M1', 'websocket':'1.3.0.M1', 'http':'1.3.0.M1', 'syslog':'1.3.0.M1', 'filter':'1.3.0.M1', 'gpfdist':'1.3.0.M1', 'splitter':'1.3.0.M1', 'jms':'1.3.0.M1']

    public static final Map<String, String> RELEASE_PHASE4_JOBS = ['groovy-filter':'1.3.0.M1', 'redis-pubsub':'1.3.0.M1', 'aggregate-counter':'1.3.0.M1', 'tasklauncher-yarn':'1.3.0.M1', 'tasklauncher-cloudfoundry':'1.3.0.M1', 'httpclient':'1.3.0.M1', 'twitter':'1.3.0.M1', 'counter':'1.3.0.M1']

    public static final Map<String, String> RELEASE_PHASE5_JOBS = ['cassandra':'1.3.0.M1', 'aggregator':'1.3.0.M1', 'mqtt':'1.3.0.M1', 'rabbit':'1.3.0.M1', 'scriptable-transform':'1.0.0.M1', 'ftp':'1.3.0.M1','file':'1.3.0.M1','aws-s3':'1.3.0.M1']

    public static final Map<String, String> RELEASE_PHASE6_JOBS = ['sftp':'1.3.0.M1', 'tensorflow':'1.3.0.M1', 'mongodb':'1.3.0.M1', 'hdfs':'1.3.0.M1', 'jdbc':'1.0.0.M1', 'gemfire':'1.3.0.M1','tcp':'1.3.0.M1','python':'1.3.0.M1']

    public static final Map<String, String> RELEASE_ALL_JOBS =
            RELEASE_PHASE1_JOBS + RELEASE_PHASE2_JOBS +
                    RELEASE_PHASE3_JOBS + RELEASE_PHASE4_JOBS +
                    RELEASE_PHASE5_JOBS + RELEASE_PHASE6_JOBS;

}
