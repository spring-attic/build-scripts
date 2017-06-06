package springcommon

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.common.view.DashboardViewBuilder

DslFactory dsl = this

new DashboardViewBuilder(this as DslFactory).buildDashboard()

dsl.listView('Seeds') {
	jobs {
		regex('.*-seed')
	}
	columns defaultColumns()
}

dsl.nestedView('Common') {
	views {
		listView('Common Libraries') {
			jobs {
				regex('spring-jenkins-common-ci')
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