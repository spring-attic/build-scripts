package io.springframework.springio.common

import io.springframework.common.Notification

/**
 * A trait to append notifications to Slack
 *
 * @author Marcin Grzejszczak
 */
trait SpringIoNotification extends Notification {

	void appendSlackNotificationForSpring(Node rootNode) {
		appendSlackNotification(rootNode, "spring-firehose")
	}

}
