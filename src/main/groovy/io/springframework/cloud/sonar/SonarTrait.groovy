package io.springframework.cloud.sonar

/**
 * @author Marcin Grzejszczak
 */
trait SonarTrait {
	void appendSonar(Node rootNode) {
		Node propertiesNode = rootNode / 'buildWrappers'
		propertiesNode / 'hudson.plugins.sonar.SonarBuildWrapper'
	}
}
