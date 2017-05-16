package io.springframework.cloud.common

import io.springframework.common.job.SlackPlugin

/**
 *
 * @author Marcin Grzejszczak
 */
class SpringCloudNotification {

	public static final String CLOUD_ROOM = "spring-cloud-firehose"

	static void cloudSlack(Node rootNode) {
		SlackPlugin.slackNotification(rootNode) {
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
}
