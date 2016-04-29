package io.springframework.common

/**
 * @author Marcin Grzejszczak
 */
trait CronTrait {
	String every15Minutes() {
		return "H/15 * * * *"
	}
	String oncePerDay() {
		return "H H * * *"
	}

	String everySunday() {
		return "H H * * 7"
	}
}