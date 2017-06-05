package springcloudappstartermavenplugins

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

dsl.nestedView('SpringCloudAppStarterMavenPlugins') {
    views {
        listView('CI') {
            jobs {
                regex('spring-cloud-app-starter.*-ci')
            }
            columns defaultColumns()
        }
        listView('Spring Cloud App Starter Maven Plugin') {
            jobs {
                regex('spring-cloud-app-starter.*')
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