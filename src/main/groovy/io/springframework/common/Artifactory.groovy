package io.springframework.common

/**
 * A trait to append Artifactory Maven Build
 *
 * @author Marcin Grzejszczak
 */
class Artifactory {

	private static String DEFAULT_ARTIFACTORY_NAME = '-1638662726@1458342414489'
	private static String DEFAULT_ARTIFACTORY_URL = 'https://repo.spring.io'

	static void artifactoryMavenBuild(Node rootNode, @DelegatesTo(ArtifactoryMaven) Closure closure) {
		ArtifactoryMaven maven = new ArtifactoryMaven(rootNode)
		closure.delegate = maven
		closure.call()
	}

	static void artifactoryMavenBuild(Node rootNode) {
		artifactoryMavenBuild(rootNode, Closure.IDENTITY)
	}

	static void artifactoryMaven3Configurator(Node rootNode, @DelegatesTo(ArtifactoryMaven3Build) Closure closure) {
		ArtifactoryMaven3Build maven = new ArtifactoryMaven3Build(rootNode)
		closure.delegate = maven
		closure.call()
	}
	static void artifactoryMaven3Configurator(Node rootNode) {
		artifactoryMaven3Configurator(rootNode, Closure.IDENTITY)
	}

	static class ArtifactoryMaven implements Maven {
		private final Node rootNode
		private final def builder

		ArtifactoryMaven(Node rootNode) {
			this.rootNode = rootNode
			Node propertiesNode = rootNode / 'builders'
			this.builder = propertiesNode / 'org.jfrog.hudson.maven3.Maven3Builder'
		}

		void setMavenVersion(String mavenVersion) {
			(builder / 'mavenName').setValue(mavenVersion)
		}

		void setGoals(String goals) {
			(builder / 'goals').setValue(goals)
		}

		void setRootPom(String rootPom) {
			(builder / 'rootPom').setValue(rootPom)
		}

		void setMavenOpts(String mavenOpts) {
			(builder / 'mavenOpts').setValue(mavenOpts)
		}
	}

	static class ArtifactoryMaven3Build implements Maven {
		private final Node rootNode
		private final def configurator
		private final def details
		private final def resolverDetails

		ArtifactoryMaven3Build(Node rootNode) {
			this.rootNode = rootNode
			Node propertiesNode = rootNode / 'buildWrappers'
			this.configurator = propertiesNode / 'org.jfrog.hudson.maven3.ArtifactoryMaven3Configurator'
			this.details = configurator / 'details'
			this.resolverDetails = configurator / 'resolverDetails'
			setArtifactoryName()
			setArtifactoryUrl()
			setDeploySnapshotRepository()
			setDeployReleaseRepository()
			setExcludePatterns()
			deployArtifacts()
			deployBuildInfo()
			filterExcludedArtifactsFromBuild()
		}

		void setArtifactoryName(String name = DEFAULT_ARTIFACTORY_NAME) {
			(details / 'artifactoryName').setValue(name)
			(resolverDetails / 'artifactoryName').setValue(name)
		}

		void setArtifactoryUrl(String url = DEFAULT_ARTIFACTORY_URL) {
			(details / 'artifactoryUrl').setValue(url)
			(resolverDetails / 'artifactoryUrl').setValue(url)
		}

		void setDeployReleaseRepository(String name = 'libs-release-local') {
			def deployReleaseRepository = details / 'deployReleaseRepository'
			(deployReleaseRepository / 'keyFromSelect').setValue(name)
		}

		void setDeploySnapshotRepository(String name = 'libs-snapshot-local') {
			def deploySnapshotRepository = details / 'deploySnapshotRepository'
			(deploySnapshotRepository / 'keyFromSelect').setValue(name)
		}

		void setExcludePatterns(String excludePatterns = '') {
			(configurator / 'artifactDeploymentPatterns' / 'excludePatterns').setValue(excludePatterns)
		}

		void deployArtifacts(boolean deploy = true) {
			(configurator / 'deployArtifacts').setValue(deploy)
		}

		void deployBuildInfo(boolean deploy = true) {
			(configurator / 'deployBuildInfo').setValue(deploy)
		}

		void filterExcludedArtifactsFromBuild(boolean filter = true) {
			(configurator / 'filterExcludedArtifactsFromBuild').setValue(filter)
		}
	}

}
