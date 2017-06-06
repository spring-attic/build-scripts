package dataflowacceptancetests

import org.springframework.jenkins.dataflowacceptancetests.ci.ScdfAcceptanceTestsPhasedBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

new ScdfAcceptanceTestsPhasedBuildMaker(dsl).build(
        ['phase-1-kubernetes-rabbit':['TestsKubernetesRabbitGroup1':['command':'run.sh -p kubernetes -b rabbit -tests ApplicationTests,PlatformHelperTests,TimestampTaskTests,TickTockTests,HttpSourceTests,TransformTests',
                                                                     'envVars':[KUBERNETES_NAMESPACE: 'jenkins-group1',
                                                                                DEPLOY_PAUSE_TIME: '10']],
                                      'TestsKubernetesRabbitGroup2':['command':'run.sh -p kubernetes -b rabbit -tests NamedChannelTests',
                                                                     'envVars':[KUBERNETES_NAMESPACE: 'jenkins-group2',
                                                                                DEPLOY_PAUSE_TIME: '10']]],
        'phase-2-kubernetes-kafka':['TestsKubernetesKafkaGroup1':['command':'run.sh -p kubernetes -b kafka -tests ApplicationTests,PlatformHelperTests,TimestampTaskTests,TickTockTests,HttpSourceTests,TransformTests',
                                                                     'envVars':[KUBERNETES_NAMESPACE: 'jenkins-group1',
                                                                                DEPLOY_PAUSE_TIME: '10']],
                                     'TestsKubernetesKafkaGroup2':['command':'run.sh -p kubernetes -b kafka -tests NamedChannelTests',
                                                                    'envVars':[KUBERNETES_NAMESPACE: 'jenkins-group2',
                                                                               DEPLOY_PAUSE_TIME: '10']]]],
        [WAIT_TIME: '30',
         GCLOUD_PROJECT:'triple-rookery-118122',
         GCLOUD_COMPUTE_ZONE:'us-central1-b',
         GCLOUD_CONTAINER_CLUSTER:'ci-cluster-3'])
