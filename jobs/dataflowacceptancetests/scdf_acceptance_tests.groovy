package dataflowacceptancetests

import io.springframework.dataflowacceptancetests.ci.ScdfAcceptanceTestsPhasedBuildMaker
import io.springframework.springcloudstream.ci.SpringCloudStreamBuildMarker
import io.springframework.springcloudstream.ci.SpringCloudStreamPhasedBuildMaker
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
new ScdfAcceptanceTestsPhasedBuildMaker(dsl).build()
