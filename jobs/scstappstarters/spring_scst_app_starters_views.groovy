package scstappstarters

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

dsl.nestedView('SpringScstAppStarters') {
	views {
		listView('Master-CI') {
			jobs {
				regex('spring-scst-app-starters.*master-ci')
			}
			columns defaultColumns()
		}
		listView('1.3.x-CI') {
			jobs {
				regex('spring-scst-app-starters.*1.3.x-ci')
			}
			columns defaultColumns()
		}
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