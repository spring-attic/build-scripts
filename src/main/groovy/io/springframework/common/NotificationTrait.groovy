package io.springframework.common

/**
 * @author Marcin Grzejszczak
 */
trait NotificationTrait {

	void appendSlackNotificationForSpringCloud(Node rootNode) {
		appendSlackNotification(rootNode, "spring-cloud-firehose")
	}

	void appendSlackNotification(Node rootNode, String roomName) {
		Node propertiesNode = rootNode / 'publishers'
		def slack = propertiesNode / 'jenkins.plugins.slack.SlackNotifier'
		(slack / 'room').setValue(roomName)
		(slack / 'startNotification').setValue(false)
		(slack / 'notifySuccess').setValue(true)
		(slack / 'notifyAborted').setValue(false)
		(slack / 'notifyNotBuilt').setValue(false)
		(slack / 'notifyUnstable').setValue(true)
		(slack / 'notifyFailure').setValue(true)
		(slack / 'notifyBackToNormal').setValue(true)
		(slack / 'notifyRepeatedFailure').setValue(true)
		(slack / 'includeTestSummary').setValue(true)
		(slack / 'showCommitList').setValue(true)
	}

}
