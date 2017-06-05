package org.springframework.jenkins.dataflowacceptancetests.ci

import javaposse.jobdsl.dsl.DslFactory
/**
 * @author Soby Chacko
 */
class ScdfAcceptanceTestsPhasedBuildMaker {


    private final DslFactory dsl

    ScdfAcceptanceTestsPhasedBuildMaker(DslFactory dsl) {
        this.dsl = dsl
    }

    void build(Map<String, Map<String, String>> commands, Map<String, Object> envVariables) {
        buildAllRelatedJobs(commands, envVariables)
        dsl.multiJob("dataflow-acceptance-tests") {
            steps {
                commands.each {
                    k, v -> phase(k, 'COMPLETED') {
                        v.each {
                            k1, v1 -> phaseJob("scdf-acceptance-tests-${k1}-ci".toString()) {
                                currentJobParameters()
                            }
                        }
                    }
                }
            }
        }
    }

    void buildAllRelatedJobs(Map<String, Map<String, String>> commands, Map<String, Object> envVariables) {
        commands.each { k, v ->
            v.each { k1, v1 ->
                new ScdfAcceptanceTestsBuildMaker(dsl, "spring-cloud", "spring-cloud-dataflow-acceptance-tests")
                        .deploy(k1, v1, envVariables)
            }

        }
    }
}
