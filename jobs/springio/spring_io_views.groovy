package springio

import org.springframework.jenkins.common.view.DashboardViewBuilder
import org.springframework.jenkins.common.view.DefaultDeliveryPipelineView
import org.springframework.jenkins.springio.ci.SpringInitializrBuildMaker
import org.springframework.jenkins.springio.common.AllSpringIoJobs
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.views.NestedViewsContext

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
		def nestedView = delegate as NestedViewsContext
		DefaultDeliveryPipelineView.build(nestedView,
				'Initializr Delivery',
				"Deploy Initializr to production",
				SpringInitializrBuildMaker.jobName())
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