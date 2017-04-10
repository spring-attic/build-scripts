
job('spring-cloud-task-app-starters-seed') {
    scm {
        git {
            remote {
                github('spring-io/build-scripts')
            }
            branch('master')
            createTag(false)
        }
    }
    steps {
        gradle("clean build")
        dsl {
            external('jobs/springcloudtaskappstarters/*.groovy')
            removeAction('DISABLE')
            removeViewAction('DELETE')
            ignoreExisting(false)
            additionalClasspath([
                    'src/main/groovy', 'src/main/resources'
            ].join("\n"))
        }
    }
}
