# Useful:
#   http://www.catosplace.net/blog/2015/02/11/running-jenkins-in-docker-containers/
#   https://github.com/jenkinsci/docker#preinstalling-plugins
#   https://engineering.riotgames.com/news/jenkins-docker-proxies-and-compose

FROM jenkins:2.0
MAINTAINER Marcin Grzejszczak <mgrzejszczak@pivotal.io>

# Adding '; exit 0'. In case of SCST some of the files are missing. Don't want to fail the build due to this.
COPY seed/init.groovy /usr/share/jenkins/ref/init.groovy
COPY seed/spring-cloud-seed.groovy /usr/share/jenkins/spring-cloud-seed.groovy; exit 0
COPY seed/spring-boot-seed.groovy /usr/share/jenkins/spring-boot-seed.groovy; exit 0
COPY seed/spring-io-seed.groovy /usr/share/jenkins/spring-io-seed.groovy; exit 0
COPY seed/spring-cloud-stream-seed.groovy /usr/share/jenkins/spring-cloud-stream-seed.groovy; exit 0
COPY seed/scst-app-starters-seed.groovy /usr/share/jenkins/scst-app-starters-seed.groovy; exit 0
COPY seed/spring-cloud-task-app-starters-seed.groovy /usr/share/jenkins/spring-cloud-task-app-starters-seed.groovy; exit 0

# To print the list of plugins from a server
#
# GOTO:
# 1) https://jenkins.address/pluginManager/api/xml?depth=1
# 2) Store the XML
# 3) Run this Groovy DSL
# def root = new groovy.util.XmlSlurper().parseText(xml)
#        println root.plugin.collect {
#            "${it.shortName}:$it.version"
#        }.join('\n')
#
# To get the list of plugins from an existing server:
#  JENKINS_HOST=myhost.com:port
#  curl -sSL "http://$JENKINS_HOST/pluginManager/api/xml?depth=1&xpath=/*/*/shortName|/*/*/version&wrapper=plugins" | perl -pe 's/.*?<shortName>([\w-]+).*?<version>([^<]+)()(<\/\w+>)+/\1:\2\n/g'

COPY plugins.txt /usr/share/jenkins/plugins.txt
RUN /usr/local/bin/plugins.sh /usr/share/jenkins/plugins.txt
