package io.springframework.dataflowacceptancetests.ci

import io.springframework.common.job.BuildAndDeploy
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Soby Chacko
 */
class ScdfAcceptanceTestsPhasedBuildMaker implements BuildAndDeploy {

    public static final List<String> BINDER_PHASE_JOBS = ['spring-cloud-stream-binder-kafka', 'spring-cloud-stream-binder-rabbit',
                                            'spring-cloud-stream-binder-jms']

    private final DslFactory dsl

    ScdfAcceptanceTestsPhasedBuildMaker(DslFactory dsl) {
        this.dsl = dsl
    }

    @Override
    String projectSuffix() {
        return 'spring-cloud-stream'
    }

    void build() {
        buildAllRelatedJobs()
        dsl.multiJob(groupName) {
            steps {
                phase('spring-cloud-stream-core-phase') {
                    triggers {
                        githubPush()
                    }
                    scm {
                        git {
                            remote {
                                url "https://github.com/spring-cloud/spring-cloud-stream"
                                branch coreBranch
                            }
                        }
                    }
                    String prefixedProjectName = prefixJob("spring-cloud-stream")
                    phaseJob("${prefixedProjectName}-${coreBranch}-ci".toString()) {
                        currentJobParameters()
                    }
                }
                phase("spring-cloud-stream-binders-phase") {
                    BINDER_PHASE_JOBS.each { String project ->
                        def branch = binders.find { it.key == project }?.value
                        if (branch) {
                            String prefixedProjectName = prefixJob(project)
                            phaseJob("${prefixedProjectName}-${branch}-ci".toString()) {
                                currentJobParameters()
                            }
                        }
                    }
                }
                phase('spring-cloud-stream-starters-phase') {
                    String prefixedProjectName = prefixJob("spring-cloud-stream-starters")
                    phaseJob("${prefixedProjectName}-${releaseTrainBranch}-ci".toString()) {
                        currentJobParameters()
                    }
                }
            }
        }
    }

    void buildAllRelatedJobs() {
        //core build
        new ScdfAcceptanceTestsBuildMaker(dsl, "spring-cloud", "spring-cloud-dataflow-acceptance-tests")
                .deploy()
        //binder builds
//        def kafkaBinderBranch = binders.find { it.key == "spring-cloud-stream-binder-kafka" }?.value
//        if (kafkaBinderBranch) {
//            new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-kafka", kafkaBinderBranch, [KAFKA_TIMEOUT_MULTIPLIER: '60'])
//                    .deploy()
//            binders.remove('spring-cloud-stream-binder-kafka')
//        }
//        def rabbitBinderBranch = binders.find { it.key == "spring-cloud-stream-binder-rabbit" }?.value
//        if (rabbitBinderBranch) {
//            new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-rabbit", rabbitBinderBranch, [:])
//                    .deploy(true, false,
//                    "clean deploy -U -Pspring", "ci-docker-compose", "docker-compose-RABBITMQ.sh",
//                    "docker-compose-RABBITMQ-stop.sh")
//            binders.remove('spring-cloud-stream-binder-rabbit')
//        }
//        binders.each { k, v -> new SpringCloudStreamBuildMarker(dsl, "spring-cloud", k, v).deploy() }
//        //starter builds
//        new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-starters", releaseTrainBranch)
//                .deploy(false, true, "clean package -Pspring", null, null, null, true)
//    }
}
