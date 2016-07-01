package io.springframework.common

import groovy.transform.CompileStatic

/**
 * Contains common cron expressions
 *
 * @author Marcin Grzejszczak
 */
@CompileStatic
trait Cron {

	String every15Minutes() {
		return "H/15 * * * *"
	}

	String oncePerDay() {
		return "H H * * *"
	}

	String everySaturday() {
		return "H H * * 6"
	}

	String everySunday() {
		return "H H * * 7"
	}

	String everyThreeHours() {
		return "H H/3 * * *"
	}

	String everyXHours(int hours) {
		return "H H/${hours} * * *"
	}

	String everySixHoursStartingFrom(int startingHour) {
		return "H ${startingHour}-23/6 * * *"
	}
}