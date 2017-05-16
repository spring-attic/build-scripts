package io.springframework.common.job

/**
 * A class represents a DSL to use with Slack Notification Plugin
 *
 * @author Marcin Grzejszczak
 */
class SlackPlugin {

	static void slackNotification(Node rootNode, @DelegatesTo(Slack) Closure closure) {
		Slack slack = new Slack(rootNode)
		closure.delegate = slack
		closure.call()
	}

	static class Slack {
		private final Node rootNode
		private final def propertiesNode
		private final def slack

		Slack(Node rootNode) {
			this.rootNode = rootNode
			this.propertiesNode = rootNode / 'publishers'
			this.slack = propertiesNode / 'jenkins.plugins.slack.SlackNotifier'
			startNotification()
			notifySuccess()
			notifyAborted()
			notifyNotBuilt()
			notifyUnstable()
			notifyFailure()
			notifyBackToNormal()
			notifyRepeatedFailure()
			notifyRegression()
			includeTestSummary()
			includeFailedTests()
			showCommitList()
			commitInfoChoice()
		}

		void room(String roomName) {
			(slack / 'room').setValue(roomName)
		}

		void startNotification(boolean value = false) {
			(slack / 'startNotification').setValue(value)
		}

		void notifySuccess(boolean value = false) {
			(slack / 'notifySuccess').setValue(value)
		}

		void notifyAborted(boolean value = false) {
			(slack / 'notifyAborted').setValue(value)
		}

		void notifyNotBuilt(boolean value = false) {
			(slack / 'notifyNotBuilt').setValue(value)
		}

		void notifyUnstable(boolean value = true) {
			(slack / 'notifyUnstable').setValue(value)
		}

		void notifyFailure(boolean value = true) {
			(slack / 'notifyFailure').setValue(value)
		}

		void notifyBackToNormal(boolean value = true) {
			(slack / 'notifyBackToNormal').setValue(value)
		}

		void notifyRepeatedFailure(boolean value = true) {
			(slack / 'notifyRepeatedFailure').setValue(value)
		}

		void notifyRegression(boolean value = true) {
			(slack / 'notifyRegression').setValue(value)
		}

		void includeTestSummary(boolean value = true) {
			(slack / 'includeTestSummary').setValue(value)
		}

		void includeFailedTests(boolean value = true) {
			(slack / 'includeFailedTests').setValue(value)
		}

		void showCommitList(boolean value = true) {
			(slack / 'showCommitList').setValue(value)
		}

		void commitInfoChoice(String value = 'AUTHORS_AND_TITLES') {
			(slack / 'commitInfoChoice').setValue(value)
		}

	}

}
