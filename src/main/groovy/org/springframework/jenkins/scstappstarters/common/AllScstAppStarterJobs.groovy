package org.springframework.jenkins.scstappstarters.common

import groovy.transform.CompileStatic

/**
 * @author Soby Chacko
 */
@CompileStatic
class AllScstAppStarterJobs {

    //Removed builds for 2.0, cassnadra and gpfdist
    //temporarily removed function

    public static final List<String> PHASE1_JOBS = ['log', 'time', 'throughput', 'bridge', 'groovy-transform', 'tasklauncher-local',
                                                    'header-enricher', 'router']

    public static final List<String> PHASE2_JOBS = ['grpc', 'trigger', 'loggregator', 'load-generator', 'mail', 'field-value-counter',
                                                    'pmml', 'tasklaunchrequest-transform', 'triggertask', 'transform']

    public static final List<String> PHASE3_JOBS = ['websocket', 'http', 'syslog', 'filter', 'splitter', 'jms', 'groovy-filter',
                                                    'redis-pubsub', 'aggregate-counter', 'tcp']

    public static final List<String> PHASE4_JOBS = ['tasklauncher-yarn', 'tasklauncher-cloudfoundry', 'tasklauncher-kubernetes',
                                                    'httpclient', 'twitter', 'counter', 'aggregator', 'mqtt', 'rabbit', 'gemfire']

    public static final List<String> PHASE5_JOBS = ['scriptable-transform', 'ftp', 'file', 'aws-s3', 'sftp',
                                                    'tensorflow', 'mongodb', 'hdfs', 'jdbc', 'python']

    public static final List<String> ALL_JOBS =
            PHASE1_JOBS + PHASE2_JOBS +
                    PHASE3_JOBS + PHASE4_JOBS +
                    PHASE5_JOBS

    public static final List<List<String>> PHASES = [
            PHASE1_JOBS, PHASE2_JOBS,
            PHASE3_JOBS, PHASE4_JOBS,
            PHASE5_JOBS]

}
