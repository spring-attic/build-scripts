package springcloud

import io.springframework.cloud.view.DashboardViewBuilder
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

new DashboardViewBuilder(this).buildDashboard()

dsl.nestedView('Spring Cloud') {
	views {
		listView('Compatibility') {
			jobs {
				regex('.*-compatibility-check')
			}
			columns {
				status()
				name()
				lastSuccess()
				lastFailure()
				lastBuildConsole()
				buildButton()
			}
		}
		listView('CI') {
			jobs {
				regex('.*-ci')
			}
			columns {
				status()
				name()
				lastSuccess()
				lastFailure()
				lastBuildConsole()
				buildButton()
			}
		}
		listView('E2E') {
			jobs {
				regex('.*-e2e')
			}
			columns {
				status()
				name()
				lastSuccess()
				lastFailure()
				lastBuildConsole()
				buildButton()
			}
		}
		listView('E2E') {
			jobs {
				regex('.*-e2e')
			}
			columns {
				status()
				name()
				lastSuccess()
				lastFailure()
				lastBuildConsole()
				buildButton()
			}
		}
		listView('All Cloud') {
			jobs {
				regex('spring-cloud.*')
			}
			columns {
				status()
				name()
				lastSuccess()
				lastFailure()
				lastBuildConsole()
				buildButton()
			}
		}
	}
}