import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.plugin.JenkinsJobManagement

def cloudSeedJobScript = new File('/usr/share/jenkins/spring-cloud-seed.groovy')
def streamCloudSeedJobScript = new File('/usr/share/jenkins/spring-cloud-stream-seed.groovy')
def ioSeedJobScript = new File('/usr/share/jenkins/spring-io-seed.groovy')
def bootSeedJobScript = new File('/usr/share/jenkins/spring-boot-seed.groovy')
def jobManagement = new JenkinsJobManagement(System.out, [:], new File('.'))

new DslScriptLoader(jobManagement).with {
	runScript(streamCloudSeedJobScript.text)
	runScript(cloudSeedJobScript.text)
	runScript(ioSeedJobScript.text)
	runScript(bootSeedJobScript.text)
}

println "Downloading the custom JMH Plugin"
def url = 'https://bintray.com/marcingrzejszczak/jenkins/download_file?file_path=jmh-jenkins%2F0.0.1%2Fjmhbenchmark.hpi'
def file = new File('/var/lib/jenkins/plugins').newOutputStream()
file << new URL(url).openStream()
file.close()