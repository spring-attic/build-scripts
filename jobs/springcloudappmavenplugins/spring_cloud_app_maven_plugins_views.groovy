package springcloudappmavenplugins

import io.springframework.cloud.view.DashboardViewBuilder
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

new DashboardViewBuilder(this).buildDashboard()

dsl.listView('Seeds') {
    jobs {
        regex('.*-seed')
    }
    columns defaultColumns()
}

dsl.nestedView('SpringCloudAppMavenPlugins') {
    views {
        listView('CI') {
            jobs {
                regex('spring-cloud-app-maven-plugins.*-ci')
            }
            columns defaultColumns()
        }
        listView('Spring Cloud App Maven Plugin') {
            jobs {
                regex('spring-cloud-app-maven-plugins.*')
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