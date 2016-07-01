package springio

import io.springframework.cloud.view.DashboardViewBuilder
import io.springframework.springio.ci.SpringStarterBuildMaker
import io.springframework.springio.common.AllSpringIoJobs
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

new DashboardViewBuilder(this).buildDashboard()

dsl.listView('Seeds') {
	jobs {
		regex('.*-seed')
	}
	columns defaultColumns()
}

String initializrName = AllSpringIoJobs.getInitializrName()

dsl.nestedView('SpringIO') {
	views {
		deliveryPipelineView("Initializr Delivery") {
			pipelineInstances(0)
			showAggregatedPipeline()
			columns(1)
			updateInterval(5)
			enableManualTriggers()
			showAvatars()
			showChangeLog()
			pipelines {
				component("Deploy Initializr to production", SpringStarterBuildMaker.jobName())
			}
		}
		buildMonitorView("Initializr Deploy Monitor") {
			jobs {
				regex("^$initializrName.*\$")
			}
		}
		listView('All Spring IO') {
			jobs {
				regex('spring-io.*')
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