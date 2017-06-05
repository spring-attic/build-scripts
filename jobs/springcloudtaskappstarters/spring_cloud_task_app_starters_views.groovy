package springcloudtaskappstarters

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

dsl.nestedView('SpringCloudTaskAppStarters') {
	views {
		listView('CI') {
			jobs {
				regex('spring-cloud-task-app-starters.*-ci')
			}
			columns defaultColumns()
		}
		listView('All Spring App Starters') {
			jobs {
				regex('spring-cloud-task-app-starters.*')
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