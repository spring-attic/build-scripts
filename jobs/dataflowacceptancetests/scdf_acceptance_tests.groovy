package dataflowacceptancetests

import org.springframework.jenkins.dataflowacceptancetests.ci.ScdfAcceptanceTestsPhasedBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

new ScdfAcceptanceTestsPhasedBuildMaker(dsl).build(
        ['phase-1-kubernetes-rabbit-setup':['KubernetesRabbitSetup':'run.sh -p kubernetes -b rabbit -t -c'],
         'phase-2-kubernetes-rabbit':['HttpSourceTestsKubernetesRabbit':'run.sh -p kubernetes -b rabbit -tests HttpSourceTests',
                                 'NamedChannelTestsKubernetesRabbit':'run.sh -p kubernetes -b rabbit -tests NamedChannelTests',
                                 'TickTockTestsKubernetesRabbit':'run.sh -p kubernetes -b rabbit -tests TickTockTests',
                                 'TimestampTaskTestsKubernetesRabbit':'run.sh -p kubernetes -b rabbit -tests TimestampTaskTests',
                                 'TransformTestsKubernetesRabbit':'run.sh -p kubernetes -b rabbit -tests TransformTests'],
         'phase-3-kubernetes-rabbit-cleanup':['KubernetesRabbitCleanup':'run.sh -p kubernetes -b rabbit -t -s'],

         'phase-4-kubernetes-kafka-setup':['KubernetesKafkaSetup':'run.sh -p kubernetes -b kafka -t -c'],
         'phase-5-kubernetes-kafka':['HttpSourceTestsKubernetesKafka':'run.sh -p kubernetes -b kafka -tests HttpSourceTests',
                                      'NamedChannelTestsKubernetesKafka':'run.sh -p kubernetes -b kafka -tests NamedChannelTests',
                                      'TickTockTestsKubernetesKafka':'run.sh -p kubernetes -b kafka -tests TickTockTests',
                                      'TimestampTaskTestsKubernetesKafka':'run.sh -p kubernetes -b kafka -tests TimestampTaskTests',
                                      'TransformTestsKubernetesKafka':'run.sh -p kubernetes -b kafka -tests TransformTests'],
         'phase-6-kubernetes-kafka-cleanup':['KubernetesKafkaCleanup':'run.sh -p kubernetes -b kafka -t -s'],

        'phase-7-local-rabbit':['HttpSourceTestsLocalRabbit':'run.sh -p local -tests HttpSourceTests',
                                 'NamedChannelTestsLocalRabbit':'run.sh -p local -tests NamedChannelTests',
                                 'TickTockTestsLocalRabbit':'run.sh -p local -tests TickTockTests',
                                 'TimestampTaskTestsLocalRabbit':'run.sh -p local -tests TimestampTaskTests',
                                 'TransformTestsLocalRabbit':'run.sh -p local -tests TransformTests'],
         'phase-8-local-kafka':['HttpSourceTestsLocalKafka':'run.sh -p local -b kafka -tests HttpSourceTests',
                                'NamedChannelTestsLocalKafka':'run.sh -p local -b kafka -tests NamedChannelTests',
                                'TickTockTestsLocalKafka':'run.sh -p local -b kafka -tests TickTockTests',
                                'TimestampTaskTestsLocalKafka':'run.sh -p local -b kafka -tests TimestampTaskTests',
                                'TransformTestsLocalKafka':'run.sh -p local -b kafka -tests TransformTests']],

        [WAIT_TIME: '30',
         KUBERNETES_NAMESPACE:'jenkins',
         GCLOUD_PROJECT:'triple-rookery-118122',
         GCLOUD_COMPUTE_ZONE:'us-central1-b',
         GCLOUD_CONTAINER_CLUSTER:'ci-cluster-3'])
