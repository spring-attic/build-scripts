package org.springframework.jenkins.springio.ci

/**
 * @author Marcin Grzejszczak
 */
class PipelineDefaults {

    static Map<String, String> envVars(Map<String, String> variables) {
        Map<String, String> envs = [:]
        envs['BLUE_APP_NAME'] = variables['BLUE_APP_NAME'] ?: 'start-blue'
        envs['GREEN_APP_NAME'] = variables['GREEN_APP_NAME'] ?: 'start-green'
        envs['BLUE_APP_HOSTNAME'] = variables['BLUE_APP_HOSTNAME'] ?: 'start-staging-blue'
        envs['GREEN_APP_HOSTNAME'] = variables['GREEN_APP_HOSTNAME'] ?: 'start-staging-green'
        envs['ROUTED_HOSTNAME'] = variables['ROUTED_HOSTNAME'] ?: 'start-staging'
        envs['DOMAIN_NAME'] = variables['DOMAIN_NAME'] ?: 'cfapps.io'
        envs['JAR_LOCATION'] = variables['JAR_LOCATION'] ?: 'initializr-service/target/initializr-service.jar'
        envs['OLD_APP_INSTANCES'] = variables['OLD_APP_INSTANCES'] ?: '1'
        envs['NEW_APP_INSTANCES'] = variables['NEW_APP_INSTANCES'] ?: '2'
        envs['OLD_APP_MEMORY'] = variables['OLD_APP_MEMORY'] ?: ''
        envs['NEW_APP_MEMORY'] = variables['NEW_APP_MEMORY'] ?: ''
        envs['CF_ORG'] = variables['CF_ORG'] ?: 'spring.io'
        envs['CF_SPACE'] = variables['CF_SPACE'] ?: 'staging'
        envs['CF_API'] = variables['CF_API'] ?: 'api.run.pivotal.io'
        return envs
    }

}
