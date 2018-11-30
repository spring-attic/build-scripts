package org.springframework.jenkins.springcloudtaskappstarters.ci

import org.springframework.jenkins.springcloudtaskappstarters.common.AllSpringCloudTaskAppStarterJobs
import org.springframework.jenkins.springcloudtaskappstarters.common.SpringCloudTaskAppStarterJobs
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Soby Chacko
 */
class SpringCloudTaskAppStatersPhasedBuildMaker implements SpringCloudTaskAppStarterJobs {

    private final DslFactory dsl

    SpringCloudTaskAppStatersPhasedBuildMaker(DslFactory dsl) {
        this.dsl = dsl
    }

    void build(boolean isRelease, String releaseType, String branchToBuild = "master") {
        buildAllRelatedJobs(isRelease, releaseType, branchToBuild)
        dsl.multiJob("spring-cloud-task-app-starter-builds" + "-" + branchToBuild) {
            steps {
                if (!isRelease) {
                    phase('core-phase', 'COMPLETED') {
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
                    phase("task-app-starters-ci-group-${counter}", 'COMPLETED') {
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
                        if (branchToBuild.equals("2.0.x")) {
                            branchToBuild = "Dearborn"
                        }
                        phaseJob("${prefixedProjectName}-${branchToBuild}-ci".toString()) {
                            currentJobParameters()
                        }
                    }
                }
            }
        }
    }

    void buildAllRelatedJobs(boolean isRelease, String releaseType, String branchToBuild) {
        if (isRelease) {
            new SpringCloudTaskAppStartersBuildMaker(dsl, "spring-cloud-task-app-starters", "core", branchToBuild)
                    .deploy(false, false, false, false, false, isRelease, releaseType)
            AllSpringCloudTaskAppStarterJobs.ALL_JOBS.each { new SpringCloudTaskAppStartersBuildMaker(dsl, "spring-cloud-task-app-starters", it, branchToBuild)
                    .deploy(true, true, true, false, false, isRelease, releaseType)}
            if (branchToBuild.equals("2.0.x")) {
                branchToBuild = "Dearborn"
            }
            new SpringCloudTaskAppStartersBuildMaker(dsl, "spring-cloud-task-app-starters", "app-starters-release", branchToBuild)
                    .deploy(false, false, false, false, true, isRelease, releaseType)
        }
        else {
            new SpringCloudTaskAppStartersBuildMaker(dsl, "spring-cloud-task-app-starters", "core", branchToBuild)
                    .deploy(false, false, false, true)
            AllSpringCloudTaskAppStarterJobs.ALL_JOBS.each {
                new SpringCloudTaskAppStartersBuildMaker(dsl, "spring-cloud-task-app-starters", it, branchToBuild).deploy()
            }
            if (branchToBuild.equals("2.0.x")) {
                branchToBuild = "Dearborn"
            }
            new SpringCloudTaskAppStartersBuildMaker(dsl, "spring-cloud-task-app-starters", "app-starters-release", branchToBuild)
                    .deploy(false, false, false, true, true)
        }
    }

}
