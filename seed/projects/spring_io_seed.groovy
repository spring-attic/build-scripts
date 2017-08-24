import org.springframework.jenkins.springio.ci.PipelineDefaults

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
        environmentVariables([
            BLUE_APP_NAME: 'start-blue',
            GREEN_APP_NAME: 'start-green',
            BLUE_APP_HOSTNAME: 'start-production-blue',
            GREEN_APP_HOSTNAME: 'start-production-green',
            DOMAIN_NAME: 'spring.io',
            ROUTED_HOSTNAME: 'start',
            JAR_LOCATION: 'initializr-service/target/initializr-service.jar',
            OLD_APP_INSTANCES: '1',
            NEW_APP_INSTANCES: '2',
            OLD_APP_MEMORY: '',
            NEW_APP_MEMORY: '',
            CF_ORG: 'spring.io',
            CF_SPACE: 'production',
            CF_API: 'api.run.pivotal.io'
        ])
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
