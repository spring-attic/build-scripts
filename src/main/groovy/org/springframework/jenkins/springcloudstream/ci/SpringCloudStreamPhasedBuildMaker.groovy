package org.springframework.jenkins.springcloudstream.ci

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.springcloudstream.common.SpringCloudStreamJobs

/**
 * @author Soby Chacko
 */
class SpringCloudStreamPhasedBuildMaker implements SpringCloudStreamJobs {

    public static final List<String> BINDER_PHASE_JOBS = ['spring-cloud-stream-binder-kafka', 'spring-cloud-stream-binder-rabbit',
                                            'spring-cloud-stream-binder-jms']

    private final DslFactory dsl

    SpringCloudStreamPhasedBuildMaker(DslFactory dsl) {
        this.dsl = dsl
    }

    void build(String coreBranch = 'master', String releaseTrainBranch = 'master',
               String groupName = 'spring-cloud-stream-builds', Map<String, String> binders) {
        def bindersCopy = [:]
        bindersCopy << binders
        buildAllRelatedJobs(coreBranch, bindersCopy, releaseTrainBranch)
        dsl.multiJob(groupName) {
            steps {
                phase('spring-cloud-stream-core-phase', 'COMPLETED') {
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
                phase("spring-cloud-stream-binders-phase", 'COMPLETED') {
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

    void buildAllRelatedJobs(String coreBranch, Map<String, String> binders, String releaseTrainBranch) {
        //core build
        new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream", coreBranch)
                .deploy()
        //binder builds
        def kafkaBinderBranch = binders.find { it.key == "spring-cloud-stream-binder-kafka" }?.value
        if (kafkaBinderBranch) {
            new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-kafka", kafkaBinderBranch, [KAFKA_TIMEOUT_MULTIPLIER: '60'])
                    .deploy()
            binders.remove('spring-cloud-stream-binder-kafka')
        }
        def rabbitBinderBranch = binders.find { it.key == "spring-cloud-stream-binder-rabbit" }?.value
        if (rabbitBinderBranch) {
            new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-binder-rabbit", rabbitBinderBranch, [:])
                    .deploy(true, false,
                    "clean deploy -U -Pspring", "ci-docker-compose", "docker-compose-RABBITMQ.sh",
                    "docker-compose-RABBITMQ-stop.sh")
            binders.remove('spring-cloud-stream-binder-rabbit')
        }
        binders.each { k, v -> new SpringCloudStreamBuildMarker(dsl, "spring-cloud", k, v).deploy() }
        //starter builds
        new SpringCloudStreamBuildMarker(dsl, "spring-cloud", "spring-cloud-stream-starters", releaseTrainBranch)
                .deploy(false, true, "clean package -Pspring", null, null, null, true)
    }
}
