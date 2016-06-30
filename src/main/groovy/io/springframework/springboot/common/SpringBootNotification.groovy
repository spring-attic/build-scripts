package io.springframework.springboot.common

import io.springframework.common.Notification

/**
 * A trait to append notifications to Slack
 *
 * @author Marcin Grzejszczak
 */
trait SpringBootNotification extends Notification {

	void slackNotificationForSpring(Node rootNode) {
		slackNotification(rootNode, "spring-firehose")
	}

}
