package io.springframework.cloud.common

import io.springframework.common.Notification

/**
 * A trait to append notifications to Slack
 *
 * @author Marcin Grzejszczak
 */
trait SpringCloudNotification extends Notification {

	void appendSlackNotificationForSpringCloud(Node rootNode) {
		appendSlackNotification(rootNode, "spring-cloud-firehose")
	}

}
