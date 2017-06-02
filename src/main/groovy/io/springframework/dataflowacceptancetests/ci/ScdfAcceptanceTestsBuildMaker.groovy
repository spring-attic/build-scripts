package io.springframework.dataflowacceptancetests.ci

import io.springframework.common.job.Cron
import io.springframework.common.job.JdkConfig
import io.springframework.common.job.Maven
import io.springframework.common.job.TestPublisher
import javaposse.jobdsl.dsl.DslFactory
/**
 * @author Soby Chacko
 */
class ScdfAcceptanceTestsBuildMaker implements JdkConfig, TestPublisher, Cron, Maven {

    private final DslFactory dsl
    final String organization
    final String project
    String branchToBuild = "master"
    boolean ghPushTrigger = true

    ScdfAcceptanceTestsBuildMaker(DslFactory dsl, String organization, String project) {
        this.dsl = dsl
        this.organization = organization
        this.project = project
    }

    static String scriptToExecute(String script) {
        return """
						echo "Running script"
						bash ${script}
					"""
    }

    void deploy(String ciName, String script, Map<String, Object> envVariables) {
        String k8sCredentials = 'SCDF_ACCEPTANCE_TESTS_K8S_CREDS'
        dsl.job("scdf-acceptance-tests-${ciName}-ci") {
            if (ghPushTrigger) {
                triggers {
                    githubPush()
                }
            }
            jdk jdk8()
            wrappers {
                colorizeOutput()
                environmentVariables(envVariables)
                if (ciName.contains("Kubernetes")) {
                    credentialsBinding {
                        file(k8sCredentials, "scdf-acceptance-tests")
                    }
                }
                timeout {
                    noActivity(300)
                    failBuild()
                    writeDescription('Build failed due to timeout after {0} minutes of inactivity')
                }
            }
            scm {
                git {
                    remote {
                        url "https://github.com/${organization}/${project}"
                        branch branchToBuild
                    }
                }
            }
            steps {
                if (script != null) {
                    if (ciName.contains("Kubernetes")) {
                        shell(scriptToExecute("gcloud auth activate-service-account --key-file \$${k8sCredentials}"))
                    }
                    shell(scriptToExecute(script))
                }
            }
            publishers {
                mailer('schacko@pivotal.io', true, true)
                archiveJunit mavenJUnitResults()
            }
        }
    }
}
