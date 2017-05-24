package io.springframework.dataflowacceptancetests.ci

import io.springframework.common.job.BuildAndDeploy
import javaposse.jobdsl.dsl.DslFactory
/**
 * @author Soby Chacko
 */
class ScdfAcceptanceTestsPhasedBuildMaker implements BuildAndDeploy {


    private final DslFactory dsl

    ScdfAcceptanceTestsPhasedBuildMaker(DslFactory dsl) {
        this.dsl = dsl
    }

    @Override
    String projectSuffix() {
        return 'spring-cloud-dataflow-acceptance-tests'
    }

    void build(Map<String, List<String>> phasesInfo, Map<String, String> commands) {
        buildAllRelatedJobs(commands)
        dsl.multiJob("dataflow-acceptance-tests") {
            steps {
                String prefixedProjectName = prefixJob("spring-cloud-dataflow-acceptance-tests")
                phasesInfo.each {
                    k, v ->
                        phase(k) {
                          v.each {
                              String test ->
                                  phaseJob("${prefixedProjectName}-${test}-ci".toString()) {
                                      currentJobParameters()
                                  }
                          }
                        }
                }
            }
        }
    }

    void buildAllRelatedJobs(Map<String, String> commands) {
        commands.each { k, v ->
            new ScdfAcceptanceTestsBuildMaker(dsl, "spring-cloud", "spring-cloud-dataflow-acceptance-tests")
                    .deploy(k, v)
        }
    }
}
