package io.springframework.springcloudstream.ci

import io.springframework.springcloudstream.common.SpringCloudStreamJobs
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Soby Chacko
 */
class SpringCloudStreamPhasedBuildMaker implements SpringCloudStreamJobs {

    public static
    final List<String> BINDER_PHASE_JOBS = ['spring-cloud-stream-binder-kafka', 'spring-cloud-stream-binder-rabbit',
                                            'spring-cloud-stream-binder-jms']

    private final DslFactory dsl

    final String branchToBuild = "master"

    SpringCloudStreamPhasedBuildMaker(DslFactory dsl) {
        this.dsl = dsl
    }

    void build(String coreBranch = 'master', String kafkaBinderBranch = 'master',
               String rabbitBinderBranch = 'master', String releaseTrainBranch = 'master') {
        buildAllRelatedJobs(coreBranch, kafkaBinderBranch, rabbitBinderBranch, releaseTrainBranch)
        dsl.multiJob("spring-cloud-stream-builds") {
            steps {
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
                phase("spring-cloud-stream-binders-phase") {
                    BINDER_PHASE_JOBS.each { String project ->
                        String prefixedProjectName = prefixJob(project)
                        phaseJob("${prefixedProjectName}-${branchToBuild}-ci".toString()) {
                            currentJobParameters()
                        }
                    }
                }
                phase('spring-cloud-stream-starters-phase') {
                    String prefixedProjectName = prefixJob("spring-cloud-stream-starters")
                    phaseJob("${prefixedProjectName}-${branchToBuild}-ci".toString()) {
                        currentJobParameters()
                    }
                }
            }
        }
    }

    void buildAllRelatedJobs(String coreBranch, String kafkaBinderBranch,
                             String rabbitBinderBranch, String releaseTrainBranch) {
        //core build
        new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream", coreBranch)
                .deploy()
        //binder builds
        new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-kafka", kafkaBinderBranch, [KAFKA_TIMEOUT_MULTIPLIER: '60'])
                .deploy()
        new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-rabbit", rabbitBinderBranch, [:])
                .deploy(true, false,
                "clean deploy -U -Pspring", "ci-docker-compose", "docker-compose-RABBITMQ.sh",
                "docker-compose-RABBITMQ-stop.sh")

        //starter builds
        new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-starters", releaseTrainBranch)
                .deploy(false, true, "clean package -Pspring", null, null, null, true)

    }
}
