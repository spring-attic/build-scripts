package io.springframework.cloud.ci

import io.springframework.common.JdkConfig
import io.springframework.common.Notification
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class CloudDeployBuildMaker implements Notification, JdkConfig {
	private final DslFactory dsl

	CloudDeployBuildMaker(DslFactory dsl) {
		this.dsl = dsl
	}

	void deploy(String project) {
		dsl.job("$project-ci") {
			triggers {
				githubPush()
			}
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/spring-cloud/${project}"
						branch 'master'
					}
					createTag(false)
				}
			}
			steps {
				shell('''
					./mvnw install -P docs -q -U -DskipTests=true -Dmaven.test.redirectTestOutputToFile=true
					./docs/src/main/asciidoc/ghpages.sh
					''')
				shell('''
					VERSION=$(mvn validate | grep Building | head -1 | sed -e 's/.* //')
					MILESTONE=$(echo ${VERSION} | egrep 'M|RC' && echo true || echo false)
					MVN_PROFILE=$([ "${MILESTONE}" == "true" ] && echo -P milestone)

					./mvnw -s .settings.xml deploy $MVN_PROFILE -nsu -Dmaven.test.redirectTestOutputToFile=true
					''')
			}

			configure {
				appendSlackNotificationForSpringCloud(it as Node)
			}
		}
	}
}
