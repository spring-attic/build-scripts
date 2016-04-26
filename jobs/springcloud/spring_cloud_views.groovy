package springcloud

import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

dsl.nestedView('Spring Cloud') {
	views {
		listView('Compatibility Jobs') {
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
		listView('CI Jobs') {
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
		listView('E2E Jobs') {
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
	}
}