package springcloudtaskappstarters

import org.springframework.jenkins.springcloudtaskappstarters.ci.SpringCloudTaskAppStatersPhasedBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

String releaseType = "" // possible values are - milestone or ga

// CI builds for spring cloud task app starters

// for a single build like CTR 1.3.1.RELEASE, do the follwoing -
// this will make the other builds go stale, but thats ok as this is temporary for the release.
//new SpringCloudTaskAppStatersPhasedBuildMaker(dsl).build(true, "ga", "1.3.x")

// for release train one off - this will make the other builds go stale, but thats ok as this is temporary for the release.
//new SpringCloudTaskAppStatersPhasedBuildMaker(dsl).build(true, "ga", "Clark")

// master builds
new SpringCloudTaskAppStatersPhasedBuildMaker(dsl).build(false, "")

// Dearborn builds
new SpringCloudTaskAppStatersPhasedBuildMaker(dsl).build(false, "", "2.0.x")