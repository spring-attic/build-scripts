package io.springframework.common

/**
 * A trait to append Artifactory Maven Build
 *
 * @author Marcin Grzejszczak
 */
trait Artifactory {

	String artifactoryName() {
		return '-1638662726@1458342414489'
	}

	String artifactoryUrl() {
		return 'https://repo.spring.io'
	}

	void artifactoryMavenBuild(Node rootNode, String mavenVersion, String mavenGoals, String mavenOpts) {
		Node propertiesNode = rootNode / 'builders'
		def builder = propertiesNode / 'org.jfrog.hudson.maven3.Maven3Builder'
		(builder / 'mavenName').setValue(mavenVersion)
		(builder / 'goals').setValue(mavenGoals)
		if (mavenOpts) {
			(builder / 'mavenOpts').setValue(mavenOpts)
		}
	}

	void artifactoryMavenBuild(Node rootNode, String mavenVersion, String mavenGoals) {
		artifactoryMavenBuild(rootNode, mavenVersion, mavenGoals)
	}

	void artifactoryMaven3Configurator(Node rootNode) {
		artifactoryMaven3Configurator(rootNode, '')
	}

	void artifactoryMaven3Configurator(Node rootNode, String excludePatterns) {
		Node propertiesNode = rootNode / 'buildWrappers'
		def configurator = propertiesNode / 'org.jfrog.hudson.maven3.ArtifactoryMaven3Configurator'
		def details = configurator / 'details'
		(details / 'artifactoryName').setValue(artifactoryName())
		(details / 'artifactoryUrl').setValue(artifactoryUrl())
		def deployReleaseRepository = details / 'deployReleaseRepository'
		(deployReleaseRepository / 'keyFromSelect').setValue('libs-release-local')
		def deploySnapshotRepository = details / 'deploySnapshotRepository'
		(deploySnapshotRepository / 'keyFromSelect').setValue('libs-snapshot-local')
		def resolverDetails = configurator / 'resolverDetails'
		(resolverDetails / 'artifactoryName').setValue(artifactoryName())
		(resolverDetails / 'artifactoryUrl').setValue(artifactoryUrl())
		(configurator / 'deployArtifacts').setValue(true)
		(configurator / 'deployBuildInfo').setValue(true)
		(configurator / 'filterExcludedArtifactsFromBuild').setValue(true)
		if (excludePatterns) {
			(configurator / 'artifactDeploymentPatterns' / 'excludePatterns').setValue(excludePatterns)
		}
	}

}
