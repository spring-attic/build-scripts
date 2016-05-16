package springcloud

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

dsl.nestedView('Spring Cloud') {
	views {
		listView('Compatibility') {
			jobs {
				regex('spring-cloud.*-compatibility-check')
			}
			columns defaultColumns()
		}
		listView('Boot Compatibility') {
			jobs {
				name('spring-cloud-compatibility-boot-check')
			}
			columns defaultColumns()
		}
		listView('CI') {
			jobs {
				regex('spring-cloud.*-ci')
			}
			columns defaultColumns()
		}
		listView('E2E') {
			jobs {
				regex('spring-cloud.*-e2e')
			}
			columns defaultColumns()
		}
		listView('Sonar') {
			jobs {
				regex('spring-cloud.*-sonar')
			}
			columns defaultColumns()
		}
		listView('F2F') {
			jobs {
				regex('spring-cloud.*-f2f')
			}
			columns defaultColumns()
		}
		listView('All Cloud') {
			jobs {
				regex('spring-cloud.*')
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