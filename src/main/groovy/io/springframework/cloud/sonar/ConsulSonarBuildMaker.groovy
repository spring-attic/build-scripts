package io.springframework.cloud.sonar

import io.springframework.cloud.common.ConsulTrait
import javaposse.jobdsl.dsl.DslFactory

/**
 * @author Marcin Grzejszczak
 */
class ConsulSonarBuildMaker extends SonarBuildMaker implements ConsulTrait {

	ConsulSonarBuildMaker(DslFactory dsl) {
		super(dsl)
	}

	void buildSonar() {
		super.buildSonar('spring-cloud-consul')
	}

	@Override
	Closure defaultSteps() {
		return buildStep {
			conditionalSteps {
				condition {
					alwaysRun()
					shell postConsulShell()
				}
			}
		} << super.defaultSteps() <<  buildStep {
			shell preConsulShell()
		}
	}
}
