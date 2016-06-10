package io.springframework.cloud.common

/**
 * @author Marcin Grzejszczak
 */
trait SpringCloudJobs {

	String prefixJob(String projectName) {
		return projectName.startsWith('spring-cloud') ? projectName : "spring-cloud-${projectName}"
	}

	String cleanup() {
		return '''
					echo "Clearing the installed cloud artifacts"
					rm -rf ~/.m2/repository/org/springframework/cloud/
					rm -rf ~/.gradle/caches/modules-2/files-2.1/org.springframework.cloud/
					'''
	}

	String buildDocs() {
		return '''
					./mvnw clean install -P docs -pl docs -q -U -DskipTests=true -Dmaven.test.redirectTestOutputToFile=true
					./docs/src/main/asciidoc/ghpages.sh
					git reset --hard && git checkout master && git reset --hard origin/master && git pull origin master
					'''
	}

	String cleanAndDeploy() {
		return '''./mvnw clean deploy -nsu -Dmaven.test.redirectTestOutputToFile=true'''
	}

}