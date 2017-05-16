package io.springframework.cloud.common

import io.springframework.common.job.SlackPlugin

/**
 *
 * @author Marcin Grzejszczak
 */
class SpringCloudNotification {

	public static final String CLOUD_ROOM = "spring-cloud-firehose"
	public static final String STREAM_ROOM = "s-c-stream-firehose"

	static SlackPlugin.Slack cloudSlack(Node rootNode) {
		return SlackPlugin.slackNotification(rootNode) {
			room(CLOUD_ROOM)
			notifySuccess(false)
			notifyAborted(false)
			notifyNotBuilt(false)
			notifyUnstable(true)
			notifyRegression(true)
			notifyFailure(false)
			notifyBackToNormal(true)
			notifyRepeatedFailure(true)
			includeTestSummary(true)
			includeFailedTests(true)
		}
	}

	static SlackPlugin.Slack cloudSlack(Node rootNode, @DelegatesTo(SlackPlugin.Slack) Closure closure) {
		SlackPlugin.Slack slack = cloudSlack(rootNode)
		closure.delegate = slack
		closure.call()
		return slack
	}
}
