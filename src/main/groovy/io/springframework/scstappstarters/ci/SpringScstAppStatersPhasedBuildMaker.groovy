package io.springframework.scstappstarters.ci

import io.springframework.scstappstarters.common.AllScstAppStarterJobs
import io.springframework.scstappstarters.common.SpringScstAppStarterJobs
import javaposse.jobdsl.dsl.DslFactory
/**
 * @author Soby Chacko
 */
class SpringScstAppStatersPhasedBuildMaker implements SpringScstAppStarterJobs {

    private final DslFactory dsl

    final String branchToBuild = "master"
    final String coreProject = "core"

    SpringScstAppStatersPhasedBuildMaker(DslFactory dsl) {
        this.dsl = dsl
    }

    void build() {
        buildAllRelatedJobs()
//        dsl.multiJob("${prefixJob(coreProject)}-${branchToBuild}-ci") {
//            steps {
//                phase('phase-1-jobs') {
//                    (AllScstAppStarterJobs.PHASE1_JOBS).each { String projectName ->
//                        String prefixedProjectName = prefixJob(projectName)
//                        phaseJob("${prefixedProjectName}-${branchToBuild}".toString()) {
//                        }
//                    }
//                }
//                phase('phase-2-jobs') {
//                    (AllScstAppStarterJobs.PHASE2_JOBS).each { String projectName ->
//                        String prefixedProjectName = prefixJob(projectName)
//                        phaseJob("${prefixedProjectName}-${branchToBuild}".toString()) {
//                        }
//                    }
//                }
//            }
//        }
    }

    void buildAllRelatedJobs() {
        new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", "core").deployNonAppStarters()
        AllScstAppStarterJobs.ALL_JOBS.each {
            new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", it).deploy()
        }
    }

}
