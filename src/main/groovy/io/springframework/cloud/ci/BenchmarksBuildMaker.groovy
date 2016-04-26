package io.springframework.cloud.ci

import io.springframework.common.NotificationTrait
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class BenchmarksBuildMaker implements NotificationTrait {
	private final DslFactory dsl

	BenchmarksBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void buildSleuth(String cronExpr = "0 0 3 1/1 * ? *") {
		dsl.job('spring-cloud-sleuth-benchmark-ci') {
			triggers {
				cron cronExpr
			}
			scm {
				git {
					remote {
						url "https://github.com/spring-cloud/spring-cloud-sleuth"
					}
					createTag(false)
				}
			}
			steps {
				shell('''
				echo "Running JMeter benchmarks"
				./scripts/runJmeterBenchmarks.sh
				''')
				shell('''
				echo "Copying JMeter results"
				mkdir -p results/benchmarks
				cp -avr benchmarks/target/ results/benchmarks/
				''')
				shell('''
				echo "Running JMH benchmark tests"
				./scripts/runJmhBenchmarks.sh
				''')
				shell('''
				echo "Copying Benchmarks results"
				mkdir -p results/jmh
				cp -avr target/benchmarks.log results/jmh/
				''')
			}
			publishers {
				archiveArtifacts('results/benchmarks/target/jmeter/results/*.png')
				archiveArtifacts('results/benchmarks/target/jmeter/results/analysis/*.*')
				archiveArtifacts('results/jmh/benchmarks.log')
			}
			configure {
				appendSlackNotificationForSpringCloud(it as Node)
			}
		}
	}
}
