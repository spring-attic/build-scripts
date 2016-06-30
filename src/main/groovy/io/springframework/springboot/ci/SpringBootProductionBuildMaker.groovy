package io.springframework.springboot.ci

import io.springframework.common.Artifactory
import io.springframework.common.Cron
import io.springframework.common.JdkConfig
import io.springframework.common.TestPublisher
import io.springframework.springboot.common.SpringBootJobs
import io.springframework.springboot.common.SpringBootNotification
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class SpringBootProductionBuildMaker implements SpringBootNotification, JdkConfig, TestPublisher,
		Cron, SpringBootJobs, Artifactory {
	private final DslFactory dsl
	final String organization

	SpringBootProductionBuildMaker(DslFactory dsl) {
		this.dsl = dsl
		this.organization = 'spring-io'
	}

	SpringBootProductionBuildMaker(DslFactory dsl, String organization) {
		this.dsl = dsl
		this.organization = organization
	}

	void deploy(String project, boolean checkTests = true) {
		dsl.job("${prefixJob(project)}-production") {
			jdk jdk8()
			scm {
				git {
					remote {
						url "https://github.com/${organization}/${project}"
						branches "master"
					}
				}
			}
			steps {
				shell('''
						echo "Building service"
						version=1.3.5.RELEASE
						if [ ! -d "spring-$version" ]; then
						  wget http://repo.spring.io/release/org/springframework/boot/spring-boot-cli/$version/spring-boot-cli-$version-bin.zip
						  unzip spring-boot-cli-$version-bin.zip
						fi
						spring-$version/bin/spring jar --exclude 'spring/**,start.jar' start.jar *.groovy
						''')
				shell('''
						echo "Building service"
						cat > deploy_script.sh <<EOF
						#!/bin/bash

						domain=start
						if [ "\\$3" != "production" ]; then domain="start-\\${3}"; fi
						echo HOST: \\${domain}

						cf api --skip-ssl-validation api.run.pivotal.io
						cf login -u \\$1 -p \\$2 -o spring.io -s $3
						cf push start -p start.jar -n \\${domain}

						EOF

						cat deploy_script.sh

						/bin/bash deploy_script.sh $*

						## catch error in script we are calling
						rc=$?
						if [[ $rc != 0 ]] ; then
							exit $rc
						fi

						rm -f deploy_script.sh
						''')
			}
			configure {
				slackNotificationForSpring(it as Node)
			}
			if (checkTests) {
				publishers {
					archiveJunit mavenJUnitResults()
				}
			}
		}
	}

	void deployWithoutTests(String project) {
		deploy(project, false)
	}
}
