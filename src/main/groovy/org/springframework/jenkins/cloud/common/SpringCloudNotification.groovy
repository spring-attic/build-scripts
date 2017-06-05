package org.springframework.jenkins.cloud.common

import org.springframework.jenkins.common.job.Slack
import org.springframework.jenkins.common.job.SlackPlugin

/**
 *
 * @author Marcin Grzejszczak
 */
class SpringCloudNotification {

	public static final String CLOUD_ROOM = "spring-cloud-firehose"
	public static final String STREAM_ROOM = "s-c-stream-firehose"

	static Slack cloudSlack(Node rootNode) {
		return SlackPlugin.slackNotification(rootNode) {
			room(CLOUD_ROOM)
			notifySuccess(false)
			notifyAborted(false)
			notifyNotBuilt(false)
			notifyUnstable(true)
			notifyRegression(false)
			notifyFailure(false)
			notifyBackToNormal(true)
			notifyRepeatedFailure(true)
			includeTestSummary(true)
			includeFailedTests(true)
		}
	}

	static Slack cloudSlack(Node rootNode, @DelegatesTo(Slack) Closure closure) {
		Slack slack = cloudSlack(rootNode)
		closure.delegate = slack
		closure.call()
		return slack
	}
}
