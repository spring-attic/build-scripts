package dataflowacceptancetests

import io.springframework.dataflowacceptancetests.ci.ScdfAcceptanceTestsPhasedBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// Master builds
/*
run.sh -p local -b rabbit -tests=collectionOfTests1
run.sh -p local -b rabbit -tests=collectionOfTests2
run.sh -p local -b rabbit -tests=collectionOfTests3
run.sh -p local -b rabbit -tests=collectionOfTests4

run.sh -p local -b kafka
run.sh -p local -b kafka
run.sh -p local -b kafka
run.sh -p local -b kafka*/
new ScdfAcceptanceTestsPhasedBuildMaker(dsl).build(
        ['phase-1':['HttpSourceTests','TapTests'],
         'phase-2':['TickTockTests','TimestampTaskTests'],
         'phase-3':['TransformTests']],

        ['HttpSourceTests':'run.sh -p local -tests HttpSourceTests',
         'TapTests':'run.sh -p local -tests TapTests',
         'TickTockTests':'run.sh -p local -tests TickTockTests',
         'TimestampTaskTests':'run.sh -p local -tests TimestampTaskTests',
         'TransformTests':'run.sh -p local -tests TransformTests'])
