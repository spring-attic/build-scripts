package io.springframework.springcloudtaskappstarters.ci

import io.springframework.springcloudtaskappstarters.common.AllSpringCloudTaskAppStarterJobs
import io.springframework.springcloudtaskappstarters.common.SpringCloudTaskAppStarterJobs
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Soby Chacko
 */
class SpringCloudTaskAppStatersPhasedBuildMaker implements SpringCloudTaskAppStarterJobs {

    private final DslFactory dsl

    final String branchToBuild = "master"

    SpringCloudTaskAppStatersPhasedBuildMaker(DslFactory dsl) {
        this.dsl = dsl
    }

    void build(boolean isRelease) {
        buildAllRelatedJobs(isRelease)
        dsl.multiJob("spring-cloud-task-app-starter-builds") {
            steps {
                if (!isRelease) {
                    phase('core-phase') {
                        triggers {
                            githubPush()
                        }
                        scm {
                            git {
                                remote {
                                    url "https://github.com/spring-cloud-task-app-starters/core"
                                    branch branchToBuild
                                }
                            }
                        }
                        String prefixedProjectName = prefixJob("core")
                        phaseJob("${prefixedProjectName}-${branchToBuild}-ci".toString()) {
                            currentJobParameters()
                        }
                    }
                }


                int counter = 1
                (AllSpringCloudTaskAppStarterJobs.PHASES).each { List<String> ph ->
                    phase("task-app-starters-ci-group-${counter}") {
                        ph.each {
                            String projectName ->
                                String prefixedProjectName = prefixJob(projectName)
                                phaseJob("${prefixedProjectName}-${branchToBuild}-ci".toString()) {
                                    currentJobParameters()
                                }
                        }
                    }
                    counter++;
                }

                if (!isRelease) {
                    phase('task-app-starters-release-phase') {
                        String prefixedProjectName = prefixJob("app-starters-release")
                        phaseJob("${prefixedProjectName}-${branchToBuild}-ci".toString()) {
                            currentJobParameters()
                        }
                    }
                }
            }
        }
    }

    void buildAllRelatedJobs(boolean isRelease) {
        if (isRelease) {
            new SpringCloudTaskAppStartersBuildMaker(dsl, "spring-cloud-task-app-starters", "core", isRelease,
                    "1.2.0.M1", null, null, "milestone")
                    .deploy(false, true, false, false)
//            AllSpringCloudTaskAppStarterJobs.ALL_JOBS.each {
//                new SpringCloudTaskAppStartersBuildMaker(dsl, "spring-cloud-task-app-starters", it, isRelease,
//                        "1.1.0.RC1", "1.1.0.RC1", "Addison.RC1", "milestone").deploy()
//            }
            AllSpringCloudTaskAppStarterJobs.RELEASE_ALL_JOBS.each { k, v -> new SpringCloudTaskAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", "${k}", isRelease,
                    "${v}", "1.2.0.M1", null, "milestone").deploy()}
            new SpringCloudTaskAppStartersBuildMaker(dsl, "spring-cloud-task-app-starters", "app-starters-release", isRelease,
                    null, "1.2.0.M1", "Belmont.M1", "milestone")
                    .deploy(false, false, false, true, true)
        }
        else {
            new SpringCloudTaskAppStartersBuildMaker(dsl, "spring-cloud-task-app-starters", "core")
                    .deploy(false, true, false, false)
            AllSpringCloudTaskAppStarterJobs.ALL_JOBS.each {
                new SpringCloudTaskAppStartersBuildMaker(dsl, "spring-cloud-task-app-starters", it).deploy()
            }
            new SpringCloudTaskAppStartersBuildMaker(dsl, "spring-cloud-task-app-starters", "app-starters-release")
                    .deploy(false, false, false, true, true)
        }


    }

}
