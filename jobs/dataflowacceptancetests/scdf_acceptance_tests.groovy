package dataflowacceptancetests

import org.springframework.jenkins.dataflowacceptancetests.ci.ScdfAcceptanceTestsPhasedBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

new ScdfAcceptanceTestsPhasedBuildMaker(dsl).build(
        ['phase-1-kubernetes-rabbit':['TestsKubernetesRabbitGroup1':'KUBERNETES_NAMESPACE=jenkins-group1 DEPLOY_PAUSE_TIME=10 run.sh -p kubernetes -b rabbit -tests ApplicationTests,PlatformHelperTests,TimestampTaskTests,TickTockTests,HttpSourceTests,TransformTests',
                                      'TestsKubernetesRabbitGroup2':'KUBERNETES_NAMESPACE=jenkins-group2 DEPLOY_PAUSE_TIME=10 run.sh -p kubernetes -b rabbit -tests TransformTests'],

         'phase-2-kubernetes-kafka':['TestsKubernetesKafkaGroup1':'KUBERNETES_NAMESPACE=jenkins-group1 DEPLOY_PAUSE_TIME=10 run.sh -p kubernetes -b kafka -tests ApplicationTests,PlatformHelperTests,TimestampTaskTests,TickTockTests,HttpSourceTests,TransformTests',
                                     'TestsKubernetesKafkaGroup2':'KUBERNETES_NAMESPACE=jenkins-group2 DEPLOY_PAUSE_TIME=10 run.sh -p kubernetes -b kafka -tests TransformTests'],

         'phase-3-local-rabbit':['HttpSourceTestsLocalRabbit':'run.sh -p local -tests HttpSourceTests',
                                 'NamedChannelTestsLocalRabbit':'run.sh -p local -tests NamedChannelTests',
                                 'TickTockTestsLocalRabbit':'run.sh -p local -tests TickTockTests',
                                 'TimestampTaskTestsLocalRabbit':'run.sh -p local -tests TimestampTaskTests',
                                 'TransformTestsLocalRabbit':'run.sh -p local -tests TransformTests'],
         'phase-4-local-kafka':['HttpSourceTestsLocalKafka':'run.sh -p local -b kafka -tests HttpSourceTests',
                                'NamedChannelTestsLocalKafka':'run.sh -p local -b kafka -tests NamedChannelTests',
                                'TickTockTestsLocalKafka':'run.sh -p local -b kafka -tests TickTockTests',
                                'TimestampTaskTestsLocalKafka':'run.sh -p local -b kafka -tests TimestampTaskTests',
                                'TransformTestsLocalKafka':'run.sh -p local -b kafka -tests TransformTests']],

        [WAIT_TIME: '30',
         GCLOUD_PROJECT:'triple-rookery-118122',
         GCLOUD_COMPUTE_ZONE:'us-central1-b',
         GCLOUD_CONTAINER_CLUSTER:'ci-cluster-3'])
