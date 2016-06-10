package io.springframework.cloud.ci

import groovy.transform.PackageScope
import io.springframework.cloud.common.HashicorpTrait
import io.springframework.common.Cron
import io.springframework.common.JdkConfig
import io.springframework.common.Notification
import io.springframework.common.Publisher
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
@PackageScope
abstract class AbstractHashicorpDeployBuildMaker implements Notification, JdkConfig, Publisher, HashicorpTrait, Cron {
	protected final DslFactory dsl
	protected final String organization
	protected final String project

	AbstractHashicorpDeployBuildMaker(DslFactory dsl, String organization, String project) {
		this.dsl = dsl
		this.organization = organization
		this.project = project
	}

	void deploy() {
		dsl.job("$project-ci") {
			triggers {
				cron everyThreeHours()
				githubPush()
			}
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/${organization}/${project}"
						branch 'master'
					}
				}
			}
			steps {
				shell('''
						echo "Clearing the installed cloud artifacts"
						rm -rf ~/.m2/repository/org/springframework/cloud/
						''')
				shell('''
						./mvnw install -P docs -q -U -DskipTests=true -Dmaven.test.redirectTestOutputToFile=true
						./docs/src/main/asciidoc/ghpages.sh
						git reset --hard && git checkout master && git pull origin master
					''')
				shell("""\
						${preStep()}
						./mvnw clean deploy -nsu -Dmaven.test.redirectTestOutputToFile=true || ${postStep()}
					""")
				shell postStep()
			}
			configure {
				appendSlackNotificationForSpringCloud(it as Node)
			}
			publishers {
				archiveJunit mavenJunitResults()
			}
		}
	}

	protected abstract String preStep()
	protected abstract String postStep()
}
