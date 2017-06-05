package scdataflowmetricscollector

import org.springframework.jenkins.common.view.DashboardViewBuilder
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

new DashboardViewBuilder(this).buildDashboard()

dsl.listView('Seeds') {
    jobs {
        regex('.*-seed')
    }
    columns defaultColumns()
}

dsl.nestedView('SpringCloudDataFlowMetricsCollector') {
    views {
        listView('CI') {
            jobs {
                regex('spring-cloud-dataflow-metrics-collector.*-ci')
            }
            columns defaultColumns()
        }
        listView('Spring Cloud Data Flow Metrics Collector') {
            jobs {
                regex('spring-cloud-dataflow-metrics-collector.*')
            }
            columns defaultColumns()
        }
    }
}

private Closure defaultColumns() {
    return {
        status()
        name()
        lastSuccess()
        lastFailure()
        lastBuildConsole()
        buildButton()
    }
}