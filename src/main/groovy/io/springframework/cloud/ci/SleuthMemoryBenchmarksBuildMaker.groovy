package io.springframework.cloud.ci

import io.springframework.cloud.common.SpringCloudNotification
import io.springframework.common.Cron
import io.springframework.common.JdkConfig
import io.springframework.common.SlackPlugin
import javaposse.jobdsl.dsl.DslFactory
/**
 * @author Marcin Grzejszczak
 */
class SleuthMemoryBenchmarksBuildMaker implements SpringCloudNotification, JdkConfig, Cron {
	private final DslFactory dsl

	SleuthMemoryBenchmarksBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void buildSleuth() {
		doBuildSleuth(oncePerDay())
	}

	private void doBuildSleuth(String cronExpr) {
		dsl.job('spring-cloud-sleuth-memory-benchmark-ci') {
			triggers {
				cron cronExpr
			}
			scm {
				git {
					remote {
						url "https://github.com/marcingrzejszczak/sleuth-memory-benchmarks"
						branch 'master'
					}
				}
			}
			wrappers {
				timestamps()
				colorizeOutput()
				timeout {
					noActivity(300)
					failBuild()
					writeDescription('Build failed due to timeout after {0} minutes of inactivity')
				}
			}
			jdk jdk8()
			steps {
				[100, 500, 1000, 2000, 5000].each { int no ->
					shell("""#!/bin/bash
					echo -e "Running example benchmarks with actuator for [${no}] requests"
					WITH_ACTUATOR=yes NO_OF_REQUESTS=${no} ./scripts/runAcceptanceTests.sh
					./scripts/kill.sh
					""")
					shell("""#!/bin/bash
					echo -e "Running example benchmarks without actuator for [${no}] requests"
					WITH_ACTUATOR=no NO_OF_REQUESTS=${no} ./scripts/runAcceptanceTests.sh
					./scripts/kill.sh
					""")
				}
			}
			publishers {
				archiveArtifacts('results/benchmarks/target/jmeter/results/*.png')
				archiveArtifacts('results/benchmarks/target/jmeter/results/analysis/*.*')
				archiveArtifacts('results/jmh/target/benchmarks.log')
			}
			configure {
				SlackPlugin.slackNotification(it as Node) {
					room(cloudRoom())
				}
				appendPerformancePlugin(it as Node,
						'results/benchmarks/target/jmeter/results/*.jtl')
			}
		}
	}

	private void appendPerformancePlugin(Node rootNode, String jmeterPath) {
		Node propertiesNode = rootNode / 'publishers'
		def perf = propertiesNode / 'hudson.plugins.performance.PerformancePublisher'
		(perf / 'errorFailedThreshold').setValue(0)
		(perf / 'errorUnstableThreshold').setValue(0)
		(perf / 'errorUnstableResponseTimeThreshold').setValue(0)
		(perf / 'relativeFailedThresholdPositive').setValue(20)
		(perf / 'relativeFailedThresholdNegative').setValue(0)
		(perf / 'relativeUnstableThresholdPositive').setValue(10)
		(perf / 'relativeUnstableThresholdNegative').setValue(0)
		(perf / 'nthBuildNumber').setValue(0)
		(perf / 'modeRelativeThresholds').setValue(false)
		// Average Response Time (ART), Percentile Response Time (PRT)
		(perf / 'configType').setValue('ART')
		(perf / 'modeOfThreshold').setValue(false)
		(perf / 'failBuildIfNoResultFile').setValue(true)
		(perf / 'compareBuildPrevious').setValue(false)
		(perf / 'xml').setValue('')
		(perf / 'modePerformancePerTestCase').setValue(true)
		(perf / 'modeThroughput').setValue(false)
		def parsers = perf / 'parsers'
		if (jmeterPath) {
			(parsers / 'hudson.plugins.performance.JMeterParser' / 'glob').setValue(jmeterPath)
		}
		/*if (junitPath) {
			(parsers / 'hudson.plugins.performance.JUnitParser' / 'glob').setValue(junitPath)
		}*/
	}
}
