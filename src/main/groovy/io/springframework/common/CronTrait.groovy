package io.springframework.common

/**
 * @author Marcin Grzejszczak
 */
trait CronTrait {
	String oncePerDay() {
		return "H H * * *"
	}

	String everySunday() {
		return "H H * * 7"
	}
}