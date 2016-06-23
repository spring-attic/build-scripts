
job('spring-cloud-seed') {
    scm {
        git {
            remote {
                github('spring-cloud/spring-cloud-jenkins-dsl')
            }
			branch('master')
            createTag(false)
        }
    }
    steps {
        gradle("clean build")
        dsl {
            external('jobs/springcloud/*.groovy')
            removeAction('DISABLE')
            removeViewAction('DELETE')
            ignoreExisting(false)
            additionalClasspath([
                'src/main/groovy', 'src/main/resources'
            ].join("\n"))
        }
    }
}
