package org.springframework.jenkins.scstappstarters.common

import groovy.transform.CompileStatic

/**
 * @author Soby Chacko
 */
@CompileStatic
class AllScstAppStarterJobs {

    //Removed cassandra and gpfdist from Darwin builds

    public static final List<String> PHASE1_JOBS = ['log', 'time', 'throughput', 'bridge', 'groovy-transform',
                                                    'header-enricher', 'router', 'tasklauncher-data-flow', 'grpc', 'trigger']

    public static final List<String> PHASE2_JOBS = ['loggregator', 'load-generator', 'mail',
                                                    'pmml', 'tasklaunchrequest-transform', 'triggertask', 'transform', 'websocket', 'http']

    public static final List<String> PHASE3_JOBS = ['syslog', 'filter', 'splitter', 'jms', 'groovy-filter',
                                                    'redis-pubsub', 'tcp', 'httpclient', 'cassandra']

    public static final List<String> PHASE4_JOBS = ['twitter', 'aggregator', 'mqtt', 'rabbit', 'gemfire',
                                                    'scriptable-transform', 'ftp', 'file', ]

    public static final List<String> PHASE5_JOBS = ['aws-s3', 'sftp', 'tensorflow', 'mongodb', 'hdfs', 'jdbc', 'python', 'analytics']

    public static final List<String> ALL_JOBS =
            PHASE1_JOBS + PHASE2_JOBS +
                    PHASE3_JOBS + PHASE4_JOBS +
                    PHASE5_JOBS

    public static final List<List<String>> PHASES = [
            PHASE1_JOBS, PHASE2_JOBS,
            PHASE3_JOBS, PHASE4_JOBS,
            PHASE5_JOBS]

    //Could be useful for the release as it reduces one phase
//    public static final List<String> PHASE1_JOBS = ['log', 'time', 'throughput', 'bridge', 'groovy-transform',
//                                                    'header-enricher', 'router', 'tasklauncher-data-flow', 'grpc', 'trigger', 'loggregator']
//
//    public static final List<String> PHASE2_JOBS = ['load-generator', 'mail', 'httpclient',
//                                                    'pmml', 'tasklaunchrequest-transform', 'triggertask', 'transform', 'websocket', 'http', 'syslog', 'filter']
//
//    public static final List<String> PHASE3_JOBS = ['jms', 'groovy-filter',
//                                                    'redis-pubsub', 'tcp',
//                                                    'cassandra', 'twitter', 'mqtt', 'aggregator', 'rabbit', 'splitter']
//
//    public static final List<String> PHASE4_JOBS = ['gemfire', 'scriptable-transform', 'ftp', 'file', 'aws-s3', 'sftp',
//                                                    'tensorflow', 'mongodb', 'hdfs', 'jdbc', 'python', 'analytics']
//
//    public static final List<String> ALL_JOBS =
//            PHASE1_JOBS + PHASE2_JOBS +
//                    PHASE3_JOBS + PHASE4_JOBS
//
//    public static final List<List<String>> PHASES = [
//            PHASE1_JOBS, PHASE2_JOBS,
//            PHASE3_JOBS, PHASE4_JOBS]

    public static final List<String> DARWIN_PHASE1_JOBS = ['log', 'time', 'throughput', 'bridge', 'groovy-transform', 'tasklauncher-local',
                                                    'header-enricher', 'router']

    public static final List<String> DARWIN_PHASE2_JOBS = ['grpc', 'trigger', 'loggregator', 'load-generator', 'mail', 'field-value-counter',
                                                    'pmml', 'tasklaunchrequest-transform', 'triggertask', 'transform']

    public static final List<String> DARWIN_PHASE3_JOBS = ['websocket', 'http', 'syslog', 'filter', 'splitter', 'jms', 'groovy-filter',
                                                    'redis-pubsub', 'aggregate-counter', 'tcp']

    public static final List<String> DARWIN_PHASE4_JOBS = ['tasklauncher-yarn', 'tasklauncher-cloudfoundry', 'tasklauncher-kubernetes',
                                                    'httpclient', 'twitter', 'counter', 'aggregator', 'mqtt', 'rabbit', 'gemfire']

    public static final List<String> DARWIN_PHASE5_JOBS = ['scriptable-transform', 'ftp', 'file', 'aws-s3', 'sftp',
                                                    'tensorflow', 'mongodb', 'hdfs', 'jdbc', 'python']

    public static final List<String> DARWIN_ALL_JOBS =
            DARWIN_PHASE1_JOBS + DARWIN_PHASE2_JOBS +
                    DARWIN_PHASE3_JOBS + DARWIN_PHASE4_JOBS +
                    DARWIN_PHASE5_JOBS

    public static final List<List<String>> DARWIN_PHASES = [
            DARWIN_PHASE1_JOBS, DARWIN_PHASE2_JOBS,
            DARWIN_PHASE3_JOBS, DARWIN_PHASE4_JOBS,
            DARWIN_PHASE5_JOBS]

    public static final List<String> CELSIUS_PHASE1_JOBS = ['log', 'time', 'throughput', 'bridge', 'groovy-transform', 'tasklauncher-local',
                                                    'header-enricher', 'router', 'counter']

    public static final List<String> CELSIUS_PHASE2_JOBS = ['trigger', 'loggregator', 'load-generator', 'mail', 'field-value-counter',
                                                    'pmml', 'tasklaunchrequest-transform', 'triggertask', 'transform']

    public static final List<String> CELSIUS_PHASE3_JOBS = ['websocket', 'http', 'syslog', 'filter', 'splitter', 'jms', 'groovy-filter',
                                                    'redis-pubsub', 'aggregate-counter', 'tcp']

    public static final List<String> CELSIUS_PHASE4_JOBS = ['tasklauncher-yarn', 'tasklauncher-cloudfoundry', 'cassandra', 'gpfdist',
                                                    'httpclient', 'twitter', 'aggregator', 'mqtt', 'rabbit', 'gemfire']

    public static final List<String> CELSIUS_PHASE5_JOBS = ['scriptable-transform', 'ftp', 'file', 'aws-s3', 'sftp',
                                                    'tensorflow', 'mongodb', 'hdfs', 'jdbc', 'python']

    public static final List<String> CELSIUS_ALL_JOBS =
            CELSIUS_PHASE1_JOBS + CELSIUS_PHASE2_JOBS +
                    CELSIUS_PHASE3_JOBS + CELSIUS_PHASE4_JOBS +
                    CELSIUS_PHASE5_JOBS

    public static final List<List<String>> CELSIUS_PHASES = [
            CELSIUS_PHASE1_JOBS, CELSIUS_PHASE2_JOBS,
            CELSIUS_PHASE3_JOBS, CELSIUS_PHASE4_JOBS,
            CELSIUS_PHASE5_JOBS]

}
