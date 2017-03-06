package io.springframework.springcloudstream.ci

import io.springframework.springcloudstream.common.SpringCloudStreamJobs
import javaposse.jobdsl.dsl.DslFactory
/**
 * @author Soby Chacko
 */
class SpringCloudStreamPhasedBuildMaker implements SpringCloudStreamJobs {

    public static final List<String> BINDER_PHASE_JOBS = ['spring-cloud-stream-binder-kafka', 'spring-cloud-stream-binder-rabbit',
                                                          'spring-cloud-stream-binder-jms']

    private final DslFactory dsl

    final String branchToBuild = "master"

    SpringCloudStreamPhasedBuildMaker(DslFactory dsl) {
        this.dsl = dsl
    }

    void build(boolean isRelease) {
        buildAllRelatedJobs(isRelease)
        dsl.multiJob("spring-cloud-stream-builds") {
            steps {
                if (!isRelease) {
                    phase('spring-cloud-stream-core-phase') {
                        triggers {
                            githubPush()
                        }
                        scm {
                            git {
                                remote {
                                    url "https://github.com/spring-cloud/spring-cloud-stream"
                                    branch branchToBuild
                                }
                            }
                        }
                        String prefixedProjectName = prefixJob("spring-cloud-stream")
                        phaseJob("${prefixedProjectName}-${branchToBuild}-ci".toString()) {
                            currentJobParameters()
                        }
                    }
                }


                BINDER_PHASE_JOBS.each { String ph ->
                    phase("spring-cloud-stream-binders-phase") {
                        ph.each {
                            String projectName ->
                                String prefixedProjectName = prefixJob(projectName)
                                phaseJob("${prefixedProjectName}-${branchToBuild}-ci".toString()) {
                                    currentJobParameters()
                                }
                        }
                    }
                }

                if (!isRelease) {
                    phase('spring-cloud-stream-starters-phase') {
                        String prefixedProjectName = prefixJob("spring-cloud-stream-starters")
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
//            new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", "core", isRelease,
//                    "1.2.0.M1", null, null, "milestone")
//                    .deploy(false, false, false, false)
//            AllScstAppStarterJobs.RELEASE_ALL_JOBS.each { k, v -> new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", "${k}", isRelease,
//                    "${v}", "1.2.0.M1", null, "milestone").deploy()}
//            new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", "app-starters-release", isRelease,
//                    null, "1.2.0.M1", "Bacon.M1", "milestone")
//                    .deploy(false, false, false, true, true)
        }
        else {
            //core build
            new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream")
                    .deploy()
            //binder builds
            new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-kafka", [KAFKA_TIMEOUT_MULTIPLIER: '60']).deploy()
            new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-rabbit", [:]).deploy(true, false,
                    "clean deploy -U -Pspring", "ci-docker-compose", "docker-compose-RABBITMQ.sh",
                    "docker-compose-RABBITMQ-stop.sh")
            new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-jms").deploy()
            //starter builds
            new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-starters").deploy(false, true, "clean package -Pspring", null, null, null, true)
        }
    }
}
