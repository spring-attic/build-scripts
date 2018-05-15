package org.springframework.jenkins.springcloudtaskappstarters.common

import groovy.transform.CompileStatic

/**
 * @author Soby Chacko
 */
@CompileStatic
class AllSpringCloudTaskAppStarterJobs {

    public static final List<String> PHASE1_JOBS = ['timestamp', 'composed-task-runner', 'timestamp-batch']

    public static final List<String> ALL_JOBS = PHASE1_JOBS;

    public static final List<List<String>> PHASES = [PHASE1_JOBS]

}
