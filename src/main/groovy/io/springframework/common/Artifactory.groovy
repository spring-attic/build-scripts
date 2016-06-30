package io.springframework.common

/**
 * A trait to append Artifactory Maven Build
 *
 * @author Marcin Grzejszczak
 */
trait Artifactory {

	void artifactoryMavenBuild(Node rootNode, String mavenVersion, String mavenGoals) {
		Node propertiesNode = rootNode / 'builders'
		def slack = propertiesNode / 'org.jfrog.hudson.maven3.Maven3Builder'
		(slack / 'mavenName').setValue(mavenVersion)
		(slack / 'goals').setValue(mavenGoals)
	}

}
