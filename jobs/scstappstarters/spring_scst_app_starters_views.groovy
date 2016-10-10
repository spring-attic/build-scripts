package scstappstarters

import io.springframework.cloud.view.DashboardViewBuilder
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

new DashboardViewBuilder(this).buildDashboard()

dsl.listView('Seeds') {
	jobs {
		regex('.*-starters-seed')
	}
	columns defaultColumns()
}

dsl.nestedView('SpringScstAppStarters') {
	views {
		listView('CI') {
			jobs {
				regex('spring-scst-app-starters.*-ci')
			}
			columns defaultColumns()
		}
		listView('All Spring App Starters') {
			jobs {
				regex('spring-scst-app-starters.*')
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