package io.springframework.common

/**
 * @author Marcin Grzejszczak
 */
trait BuildAndDeploy {

	String prefixJob(String projectName) {
		return projectName.startsWith(projectSuffix()) ? projectName : "${projectSuffix()}-${projectName}"
	}

	abstract String projectSuffix()

	String cleanAndDeploy() {
		return '''./mvnw clean deploy -nsu -Dmaven.test.redirectTestOutputToFile=true'''
	}

	String branchVar() {
		return 'BRANCH'
	}

	String masterBranch() {
		return 'master'
	}
}