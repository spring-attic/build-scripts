package dataflowacceptancetests

import io.springframework.dataflowacceptancetests.ci.ScdfAcceptanceTestsPhasedBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

new ScdfAcceptanceTestsPhasedBuildMaker(dsl).build(
        ['phase-1-local-rabbit':['HttpSourceTestsLocalRabbit':'run.sh -p local -tests HttpSourceTests',
                                 'TapTestsLocalRabbit':'run.sh -p local -tests TapTests',
                                 'TickTockTestsLocalRabbit':'run.sh -p local -tests TickTockTests',
                                 'TimestampTaskTestsLocalRabbit':'run.sh -p local -tests TimestampTaskTests',
                                 'TransformTestsLocalRabbit':'run.sh -p local -tests TransformTests'],
         'phase-1-local-kafka':['HttpSourceTestsLocalKafka':'run.sh -p local -b kafka -tests HttpSourceTests',
                                'TapTestsLocalKafka':'run.sh -p local -b kafka -tests TapTests',
                                'TickTockTestsLocalKafka':'run.sh -p local -b kafka -tests TickTockTests',
                                'TimestampTaskTestsLocalKafka':'run.sh -p local -b kafka -tests TimestampTaskTests',
                                'TransformTestsLocalKafka':'run.sh -p local -b kafka -tests TransformTests']])
