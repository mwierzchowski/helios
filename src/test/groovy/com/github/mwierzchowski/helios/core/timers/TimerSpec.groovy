package com.github.mwierzchowski.helios.core.timers

import spock.lang.Specification
import spock.lang.Subject

import static java.time.DayOfWeek.*
import static java.time.LocalTime.of

class TimerSpec extends Specification {
    @Subject
    Timer timer = new Timer()

    def setup() {
        timer.add new TimerSchedule().tap {
            it.id = 1
            it.time = of(8, 0)
            it.days = [MONDAY, TUESDAY]
        }
        timer.add new TimerSchedule().tap {
            it.id = 2
            it.time = of(10, 0)
            it.days = [SATURDAY]
        }
    }

    def "Should hasSame return true if one of the schedules is same logically"() {
        given:
        def schedule = new TimerSchedule().tap {
            it.time = of(8, 0)
            it.days = [MONDAY, TUESDAY]
        }
        expect:
        timer.hasSame(schedule)
    }

    def "Should hasSame return false if one of the schedules is not same logically"() {
        given:
        def schedule = new TimerSchedule().tap {
            it.time = of(8, 0)
            it.days = [MONDAY]
        }
        expect:
        !timer.hasSame(schedule)
    }

    def "Should hasOverlapping return true if one of the schedules has a day in common"() {
        given:
        def schedule = new TimerSchedule().tap {
            it.time = of(10, 0)
            it.days = [SATURDAY]
        }
        expect:
        timer.hasOverlapping(schedule)
    }

    def "Should hasOverlapping return false if one of the schedules has a day in common"() {
        given:
        def schedule = new TimerSchedule().tap {
            it.time = of(8, 0)
            it.days = [WEDNESDAY]
        }
        expect:
        !timer.hasOverlapping(schedule)
    }

    def "Should return schedule if it exists"() {
        given:
        def scheduleId = 1
        expect:
        timer.getSchedule(scheduleId).isPresent()
    }

    def "Should not return schedule if it exists"() {
        given:
        def scheduleId = 3
        expect:
        timer.getSchedule(scheduleId).isEmpty()
    }
}
