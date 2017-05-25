package dataflowacceptancetests

import io.springframework.dataflowacceptancetests.ci.ScdfAcceptanceTestsPhasedBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

new ScdfAcceptanceTestsPhasedBuildMaker(dsl).build(
        ['phase-1-kubernetes-rabbit':['HttpSourceTestsKubernetesRabbit':'run.sh -p kubernetes -tests HttpSourceTests',
                                 'NamedChannelTestsKubernetesRabbit':'run.sh -p kubernetes -tests NamedChannelTests',
                                 'TickTockTestsKubernetesRabbit':'run.sh -p local -kubernetes TickTockTests',
                                 'TimestampTaskTestsKubernetesRabbit':'run.sh -p kubernetes -tests TimestampTaskTests',
                                 'TransformTestsKubernetesRabbit':'run.sh -p kubernetes -tests TransformTests'],
         'phase-2-kubernetes-kafka':['HttpSourceTestsKubernetesKafka':'run.sh -p kubernetes -b kafka -tests HttpSourceTests',
                                'NamedChannelTestsKubernetesKafka':'run.sh -p kubernetes -b kafka -tests NamedChannelTests',
                                'TickTockTestsKubernetesKafka':'run.sh -p kubernetes -b kafka -tests TickTockTests',
                                'TimestampTaskTestsKubernetesKafka':'run.sh -p kubernetes -b kafka -tests TimestampTaskTests',
                                'TransformTestsKubernetesKafka':'run.sh -p kubernetes -b kafka -tests TransformTests'],
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

        [WAIT_TIME: '30'])
