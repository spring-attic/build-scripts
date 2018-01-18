package springcloudgcp

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

dsl.nestedView('SpringCloudDataflowSamples') {
    views {
        listView('CI') {
            jobs {
                regex('spring-cloud-dataflow-samples.*-ci')
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