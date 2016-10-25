package io.springframework.scstappstarters.common

import groovy.transform.CompileStatic

/**
 * @author Soby Chacko
 */
@CompileStatic
class AllScstAppStarterJobs {

    /**
     * List of all Spring Cloud jobs. This list will be used to create the boot compatibility builds
     * and will serve as basis for the default jobs
     */
    public static final List<String> PHASE1_JOBS = ['log', 'time']

    /**
     * List of all Spring Cloud jobs. This list will be used to create the boot compatibility builds
     * and will serve as basis for the default jobs
     */
    public static final List<String> PHASE2_JOBS = ['file']

    public static final List<String> ALL_JOBS = PHASE1_JOBS + PHASE2_JOBS

}
