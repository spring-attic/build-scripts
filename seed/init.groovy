import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.plugin.JenkinsJobManagement

def seedJobScript = new File('/usr/share/jenkins/spring-cloud-seed.groovy')
def jobManagement = new JenkinsJobManagement(System.out, [:], new File('.'))

new DslScriptLoader(jobManagement).runScript(seedJobScript.text)
