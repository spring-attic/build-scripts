import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

def springCloudBuildUrl = 'https://github.com/spring-cloud/spring-cloud-build'
def springBootVersion = '1.4.0.BUILD-SNAPSHOT'
def gistUrl = 'https://gist.githubusercontent.com/marcingrzejszczak/e63d4985f2a12d51af3310be51b2caa2/raw/c741bfea548b2b99fc242d743b1270301eab5167/replace_parent_version_in_pom.groovy'
def groovyLocation = '/opt/groovy/2.4.5/bin/groovy'
def projects = ['spring-cloud-sleuth', 'spring-cloud-netflix', 'spring-cloud-zookeeper']

projects.eachWithIndex { String projectName, Integer index ->
	dsl.job("${projectName}-compatibility-check") {
		triggers {
			cron everyDayAt(index)
		}
		scm {
			git("https://github.com/spring-cloud/$projectName")
		}
		steps {
			shell("""
					echo "Cloning spring-cloud-build"
					git clone $springCloudBuildUrl
					echo "Downloading and running script to change parent version"
					wget $gistUrl --no-check-certificate
					$groovyLocation replace_parent_version_in_pom.groovy -p "spring-cloud-build/pom.xml" -v "$springBootVersion"
					""")
			shell('''
					echo "Installing built version with different parent"
					./spring-cloud-build/mvnw clean install
					''')
			shell("""
					echo -e "Printing the list of dependencies for [$projectName]"
					./mvnw dependency:tree
					""")
			shell('''
					echo -e "Running the tests"
					./mvnw clean verify -fae
					''')
		}
	}
	dsl.folder('Spring Cloud Jobs') {

	}
}

String everyDayAt(int offset, int startingHour = 5) {
	return "0 0 ${startingHour + offset} 1/1 * ? *"
}