package springboot

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

dsl.nestedView('SpringBoot') {
	views {
		listView('CI') {
			jobs {
				regex('spring-boot.*-ci')
			}
			columns defaultColumns()
		}
		listView('Prod') {
			jobs {
				regex('spring-boot.*-production')
			}
			columns defaultColumns()
		}
		listView('All Spring Boot') {
			jobs {
				regex('spring-boot.*')
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