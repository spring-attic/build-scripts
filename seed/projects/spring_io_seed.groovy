job('spring-io-seed') {
    triggers {
        githubPush()
    }
    scm {
        git {
            remote {
                github('spring-io/build-scripts')
            }
            branch('master')
        }
    }
    wrappers {
        parameters {
            stringParam('BLUE_APP_NAME', 'start-blue', 'The name of the blue instance')
            stringParam('GREEN_APP_NAME', 'start-green', 'The name of the green instance')
            stringParam('BLUE_APP_HOSTNAME', 'start-staging-blue', 'The hostname of the blue instance')
            stringParam('GREEN_APP_HOSTNAME', 'start-staging-green', 'The hostname of the green instance')
            stringParam('ROUTED_HOSTNAME', 'start-staging', 'The hostname to which the "production" traffic gets routed')
            stringParam('DOMAIN_NAME', 'cfapps.io', 'Domain of the deployed application')
            stringParam('JAR_LOCATION', 'initializr-service/target/initializr-service.jar', 'Location of the JAR to be deployed')
            stringParam('OLD_APP_INSTANCES', '1', 'Number of instances of the old instance. If you pass [0] then the old instance will get stopped')
            stringParam('NEW_APP_INSTANCES', '2', ' Number of instances of the new instance')
            stringParam('OLD_APP_MEMORY', '', 'Memory to be used by the old instance')
            stringParam('NEW_APP_MEMORY', '', 'Memory to be used by the new instance')
            stringParam('CF_ORG', 'spring.io', 'Cloud Foundry organization to which you would like to deploy the application')
            stringParam('CF_SPACE', 'staging', 'Cloud Foundry space to which you would like to deploy the application')
            stringParam('CF_API', 'api.run.pivotal.io', 'Cloud Foundry API of the installation to which you would like to deploy the application')
        }
    }
    steps {
        gradle("clean build")
        dsl {
            external('jobs/springio/*.groovy')
            removeAction('DISABLE')
            removeViewAction('DELETE')
            ignoreExisting(false)
            additionalClasspath([
                    'src/main/groovy', 'src/main/resources', 'build/lib/*.jar'
            ].join("\n"))
        }
    }
}
