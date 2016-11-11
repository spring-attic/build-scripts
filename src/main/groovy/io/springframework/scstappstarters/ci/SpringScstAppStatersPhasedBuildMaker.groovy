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

    SpringScstAppStatersPhasedBuildMaker(DslFactory dsl) {
        this.dsl = dsl
    }

    void build(boolean isRelease) {
        buildAllRelatedJobs(isRelease)
        dsl.multiJob("spring-scst-app-starter-builds") {
            steps {
                if (!isRelease) {
                    phase('core-phase') {
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
                    phase("app-starters-ci-group-${counter}") {
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
                    phase('docs-phase') {
                        String prefixedProjectName = prefixJob("docs")
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
            new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", "core", isRelease,
                    "1.1.0.RC1", "1.1.0.RC1", "Avogadro.RC1", "milestone")
                    .deploy(false, false, false, false)
            AllScstAppStarterJobs.ALL_JOBS.each {
                new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", it, isRelease,
                        "1.1.0.RC1", "1.1.0.RC1", "Avogadro.RC1", "milestone").deploy()
            }
            new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", "docs", isRelease,
                    "1.1.0.RC1", "1.1.0.RC1", "Avogadro.RC1", "milestone")
                    .deploy(false, false, false, true, true)
        }
        else {
            new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", "core")
                    .deploy(false, false, false, false)
            AllScstAppStarterJobs.ALL_JOBS.each {
                new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", it).deploy()
            }
            new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", "app-starters-release")
                    .deploy(false, false, false, true, true)
        }


    }

}
