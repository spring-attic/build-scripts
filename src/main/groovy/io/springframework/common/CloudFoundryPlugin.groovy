package io.springframework.common

/**
 * A class represents a DSL to use with Cloud Foundry plugin
 *
 * @author Marcin Grzejszczak
 */
class CloudFoundryPlugin {

	static void pushToCloudFoundry(Node rootNode, @DelegatesTo(CloudFoundry) Closure closure) {
		CloudFoundry maven = new CloudFoundry(rootNode)
		closure.delegate = maven
		closure.call()
	}

	static class CloudFoundry {
		private final Node rootNode
		private final def builder
		private final def manifestChoice

		CloudFoundry(Node rootNode) {
			this.rootNode = rootNode
			Node publishers = rootNode / 'publishers'
			this.builder = publishers / 'com.hpe.cloudfoundryjenkins.CloudFoundryPushPublisher'
			this.manifestChoice = builder / 'manifestChoice'
			target()
			credentialsId()
			selfSigned()
			resetIfExists()
			pluginTimeout()
			servicesToCreate()
			manifestFromFile()
			noRoute()
		}

		void target(String target = 'http://api.run.pivotal.io') {
			(builder / 'target').setValue(target)
		}

		void organization(String organization) {
			(builder / 'organization').setValue(organization)
		}

		void cloudSpace(String cloudSpace) {
			(builder / 'cloudSpace').setValue(cloudSpace)
		}

		void credentialsId(String credentialsId = 'PWS buildmaster@springframework.org') {
			(builder / 'credentialsId').setValue(credentialsId)
		}

		void selfSigned(boolean value = false) {
			(builder / 'selfSigned').setValue(value)
		}

		void resetIfExists(boolean value = false) {
			(builder / 'resetIfExists').setValue(value)
		}

		void pluginTimeout(int timeout = 120) {
			(builder / 'resetIfExists').setValue(timeout)
		}

		void servicesToCreate(String... services = '') {
			(builder / 'servicesToCreate').setValue(services.join(','))
		}

		void manifestFromFile(String manifestFileName = 'manifest.yml') {
			(manifestChoice / 'value').setValue('manifestFile')
			(manifestChoice / 'manifestFile').setValue(manifestFileName)
		}

		void noRoute(boolean value = false) {
			(manifestChoice / 'noRoute').setValue(value)
		}

		void envVars(Map<String, String> vars = [:]) {
			def envVarsNode = builder / 'envVars'
			vars.each { String envKey, String envValue ->
				envVarsNode << 'com.hpe.cloudfoundryjenkins.CloudFoundryPushPublisher_-EnvironmentVariable' {
					key(envKey)
					value(envValue)
				}
			}
		}

		void serviceNames(String... services) {
			def envVarsNode = builder / 'servicesNames'
			services.each { String service ->
				envVarsNode << 'com.hpe.cloudfoundryjenkins.CloudFoundryPushPublisher_-ServiceName' {
					name(service)
				}
			}
		}

		void manifestConfig(@DelegatesTo(ManifestChoice) Closure closure) {
			(manifestChoice / 'value').setValue('jenkinsConfig')
			ManifestChoice manifestChoice = new ManifestChoice(manifestChoice)
			closure.delegate = manifestChoice
			closure.call()
		}

		class ManifestChoice {
			private final def manifestChoice

			ManifestChoice(manifestChoice) {
				this.manifestChoice = manifestChoice
				memory()
				instances()
				domain()
			}

			void appName(String appName) {
				(manifestChoice / 'appName').setValue(appName)
			}

			void memory(int memory = 512) {
				(manifestChoice / 'memory').setValue(memory)
			}

			void hostName(String hostName) {
				(manifestChoice / 'hostName').setValue(hostName)
			}

			void domain(String domain = 'cfapps.io') {
				(manifestChoice / 'domain').setValue(domain)
			}

			void instances(int instances = 1) {
				(manifestChoice / 'instances').setValue(instances)
			}

			void appPath(String appPath) {
				(manifestChoice / 'appPath').setValue(appPath)
			}

			void buildPack(String buildPack) {
				(manifestChoice / 'buildPack').setValue(buildPack)
			}

			void stack(String stack) {
				(manifestChoice / 'stack').setValue(stack)
			}

			void command(String command) {
				(manifestChoice / 'command').setValue(command)
			}
		}
	}

}
