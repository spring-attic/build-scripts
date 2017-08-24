package org.springframework.jenkins.springio.ci

/**
 * @author Marcin Grzejszczak
 */
class PipelineDefaults {

    static Map<String, String> envVars(Map<String, String> variables) {
        Map<String, String> envs = [:]
        envs['BLUE_APP_NAME'] = variables['BLUE_APP_NAME'] ?: ''
        envs['GREEN_APP_NAME'] = variables['GREEN_APP_NAME'] ?: ''
        envs['BLUE_APP_HOSTNAME'] = variables['BLUE_APP_HOSTNAME'] ?: ''
        envs['GREEN_APP_HOSTNAME'] = variables['GREEN_APP_HOSTNAME'] ?: ''
        envs['ROUTED_HOSTNAME'] = variables['ROUTED_HOSTNAME'] ?: ''
        envs['DOMAIN_NAME'] = variables['DOMAIN_NAME'] ?: ''
        envs['JAR_LOCATION'] = variables['JAR_LOCATION'] ?: ''
        envs['OLD_APP_INSTANCES'] = variables['OLD_APP_INSTANCES'] ?: ''
        envs['NEW_APP_INSTANCES'] = variables['NEW_APP_INSTANCES'] ?: ''
        envs['OLD_APP_MEMORY'] = variables['OLD_APP_MEMORY'] ?: ''
        envs['NEW_APP_MEMORY'] = variables['NEW_APP_MEMORY'] ?: ''
        envs['CF_ORG'] = variables['CF_ORG'] ?: ''
        envs['CF_SPACE'] = variables['CF_SPACE'] ?: ''
        envs['CF_API'] = variables['CF_API'] ?: ''
        return envs
    }

}
