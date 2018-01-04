package org.springframework.jenkins.scstappstarters.ci

import org.springframework.jenkins.scstappstarters.common.AllScstAppStarterJobs
import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.scstappstarters.common.SpringScstAppStarterJobs

/**
 * @author Soby Chacko
 */
class SpringScstAppStatersPhasedBuildMaker implements SpringScstAppStarterJobs {

    private final DslFactory dsl

    String branchToBuild = "1.3.x"

    SpringScstAppStatersPhasedBuildMaker(DslFactory dsl) {
        this.dsl = dsl
    }

    void build(boolean isRelease, String releaseType) {
        buildAllRelatedJobs(isRelease, releaseType)
        dsl.multiJob("spring-scst-app-starter-builds") {
            steps {
                if (!isRelease) {
                    phase('core-phase', 'COMPLETED') {
                        triggers {
                            githubPush()
                        }
                        scm {
                            git {
                                remote {
                                    url "https://github.com/spring-cloud-stream-app-starters/core"
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
                (AllScstAppStarterJobs.PHASES).each { List<String> ph ->
                    phase("app-starters-ci-group-${counter}", 'COMPLETED') {
                        ph.each {
                            String projectName ->
                                if (projectName.equals("tensorflow") ||
                                projectName.equals(("python")) ||
                                projectName.equals("mqtt")) {
                                    branchToBuild = "1.0.x"
                                }
                                else {
                                    branchToBuild = "1.3.x"
                                }
                                String prefixedProjectName = prefixJob(projectName)
                                phaseJob("${prefixedProjectName}-${branchToBuild}-ci".toString()) {
                                    currentJobParameters()
                                }
                        }
                    }
                    counter++;
                }

                if (!isRelease) {
                    phase('app-starters-release-phase') {
                        String prefixedProjectName = prefixJob("app-starters-release")
                        branchToBuild = "Celsius"
                        phaseJob("${prefixedProjectName}-${branchToBuild}-ci".toString()) {
                            currentJobParameters()
                        }
                    }
                }
            }
        }
    }

    void buildAllRelatedJobs(boolean isRelease, String releaseType) {
        if (isRelease) {
            new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", "core",
                    "1.3.0.M1", null)
                    .deploy(false, false, false, false, false, isRelease, releaseType)
            AllScstAppStarterJobs.RELEASE_ALL_JOBS.each { k, v -> new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", "${k}",
                    "1.3.0.M1", null).deploy(true, true,
                    true, false, false, isRelease, releaseType)}
            new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", "app-starters-release",
                    "1.2.0.M1", "Bacon.M1")
                    .deploy(false, false, false, false, true, isRelease, releaseType)
        }
        else {
            new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", "core")
                    .deploy(false, false, false, true, false, isRelease, releaseType)
            AllScstAppStarterJobs.ALL_JOBS.each {
                new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", it).deploy(true, true,
                true, true, false, isRelease, releaseType)
            }
            new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", "app-starters-release")
                    .deploy(false, false, false, true, true, isRelease, releaseType)
        }


    }

}
