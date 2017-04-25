package scdataflowmetricscollector

import io.springframework.scdataflowmetricscollector.ci.SpringCloudDataFlowMetricsCollectorBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

// CI
new SpringCloudDataFlowMetricsCollectorBuildMaker(dsl, "spring-cloud", "spring-cloud-dataflow-metrics-collector")
        .deploy()


