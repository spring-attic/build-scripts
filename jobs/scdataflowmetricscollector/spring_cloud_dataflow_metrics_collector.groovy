package scdataflowmetricscollector

import org.springframework.jenkins.scdataflowmetricscollector.ci.SpringCloudDataFlowMetricsCollectorBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// Snapshots CI
enableSnapshots(dsl)
//enablGARelease(dsl)

// Milestone CI
//enableMilestoneRcRelease(dsl)


void enableSnapshots(DslFactory dsl) {
    new SpringCloudDataFlowMetricsCollectorBuildMaker(dsl, "spring-cloud", "spring-cloud-dataflow-metrics-collector")
            .deploy()
}

void enableMilestoneRcRelease(DslFactory dsl) {
    new SpringCloudDataFlowMetricsCollectorBuildMaker(dsl, "spring-cloud", "spring-cloud-dataflow-metrics-collector")
            .deploy(true, false, true, false)
}

void enablGARelease(DslFactory dsl) {
    new SpringCloudDataFlowMetricsCollectorBuildMaker(dsl, "spring-cloud", "spring-cloud-dataflow-metrics-collector")
            .deploy(true, false, false, true)
}