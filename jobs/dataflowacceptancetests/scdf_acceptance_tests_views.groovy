package dataflowacceptancetests

import io.springframework.common.view.DashboardViewBuilder
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

new DashboardViewBuilder(this).buildDashboard()

dsl.listView('Seeds') {
    jobs {
        regex('.*-seed')
    }
    columns defaultColumns()
}

dsl.nestedView('SpringCloudDataFlowAcceptanceTests') {
    views {
        listView('CI') {
            jobs {
                regex('spring-cloud-dataflow-acceptance-tests.*-ci')
            }
            columns defaultColumns()
        }
        listView('Spring Cloud Data Flow Acceptance Tests') {
            jobs {
                regex('spring-cloud-dataflow-acceptance-tests.*')
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