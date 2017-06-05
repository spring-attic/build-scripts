package springcloud

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

dsl.nestedView('Spring Cloud') {
	views {
		listView('Boot.NEXT') {
			jobs {
				regex('spring-cloud.*-compatibility-check')
			}
			columns defaultColumns()
		}
		listView('Boot.MANUAL') {
			jobs {
				name('spring-cloud-compatibility-boot-check')
			}
			columns defaultColumns()
		}
		listView('Spring.NEXT') {
			jobs {
				regex('spring-cloud.*-compatibility-spring-check')
			}
			columns defaultColumns()
		}
		listView('Spring.MANUAL') {
			jobs {
				name('spring-cloud-compatibility-spring-check')
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
		nestedView('F2F-pipelines') {
			def nested = delegate
			['github-analytics','github-webhook'].each {
				String artifactName = it
				String projectName = "${artifactName}-pipeline"
				nested.views {
					deliveryPipelineView(projectName) {
						allowPipelineStart()
						pipelineInstances(5)
						showAggregatedPipeline(false)
						columns(1)
						updateInterval(5)
						enableManualTriggers()
						showAvatars()
						showChangeLog()
						pipelines {
							component("Deploy ${artifactName} to production", "${projectName}-build")
						}
						allowRebuild()
						showDescription()
						showPromotions()
						showTotalBuildTime()
						configure {
							(it / 'showTestResults').setValue(true)
							(it / 'pagingEnabled').setValue(true)
						}
					}
				}
			}
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