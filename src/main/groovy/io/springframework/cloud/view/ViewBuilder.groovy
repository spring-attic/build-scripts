package io.springframework.cloud.view

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.JobParent
import javaposse.jobdsl.dsl.View

class ViewBuilder {

	private final DslFactory dslFactory

	ViewBuilder(DslFactory dslFactory) {
		this.dslFactory = dslFactory
	}

	View buildDashboard() {
		JobParent jobParent = dslFactory as JobParent
		View view = new Dashboard(jobParent.jm)
		view.name = 'Overview'
		jobParent.referencedViews << view
		return view
	}

}