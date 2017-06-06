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

    void build(Map<String, Map<String, Map<String, Object>>> phases, Map<String, Object> envVariables) {
        buildAllRelatedJobs(phases, envVariables)
        dsl.multiJob("dataflow-acceptance-tests") {
            steps {
                phases.each {
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

    void buildAllRelatedJobs(Map<String, Map<String, Map<String, Object>>> phases, Map<String, Object> envVariables) {
        String command
        phases.each { phase, group ->
            group.each { groupName, commands ->
                commands.each {
                    k, v ->
                        switch (k) {
                            case 'envVars':
                                envVariables << (Map<String, Object>)v
                                break
                            case 'command':
                                command = v
                        }
                }
                new ScdfAcceptanceTestsBuildMaker(dsl, "spring-cloud", "spring-cloud-dataflow-acceptance-tests")
                        .deploy(groupName, command, envVariables)
            }
        }
    }
}
