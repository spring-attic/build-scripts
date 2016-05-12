#!/bin/bash

echo "Starting Jenkins for the first time"
nohup /bin/tini -- /usr/local/bin/jenkins.sh &
pid=$!
sleep 30
echo "Killing Jenkins"
kill -9 "$pid"
pkill -f 'java'

echo "Adding the config file"
rm $JENKINS_HOME/config.xml
cp /usr/share/jenkins/unsafe_config.xml $JENKINS_HOME/config.xml
ls -al $JENKINS_HOME/
cat $JENKINS_HOME/config.xml

echo "Adding seed job"
mkdir -p $JENKINS_HOME/jobs/spring-cloud-seed
cp /usr/share/jenkins/spring-cloud-seed.xml $JENKINS_HOME/jobs/spring-cloud-seed/config.xml
chown -R jenkins:jenkins $JENKINS_HOME/
ls -al $JENKINS_HOME/jobs/

echo "Running plugins"
/usr/local/bin/plugins.sh /usr/share/jenkins/plugins.txt

echo "Running Jenkins again"
pkill -f 'java'
ps -efw | grep jenkins
/bin/tini -- /usr/local/bin/jenkins.sh