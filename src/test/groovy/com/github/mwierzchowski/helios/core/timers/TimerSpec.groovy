package com.github.mwierzchowski.helios.core.timers

import spock.lang.Specification
import spock.lang.Subject

import static java.time.DayOfWeek.*
import static java.time.LocalTime.of

class TimerSpec extends Specification {
    @Subject
    Timer timer = new Timer()

    def setup() {
        timer.schedules = new LinkedHashSet<>()
        timer.schedules << TimerSchedule.builder()
                .id(1)
                .enabled(true)
                .time(of(8, 0))
                .days([MONDAY, TUESDAY] as Set)
                .build()
        timer.schedules << TimerSchedule.builder()
                .id(2)
                .enabled(false)
                .time(of(10, 0))
                .days([SATURDAY] as Set)
                .build()
    }

    def "Should hasSame return true if one of the schedules is same logically"() {
        given:
        def schedule = TimerSchedule.builder()
                .enabled(false)
                .time(of(8, 0))
                .days([MONDAY, TUESDAY] as Set)
                .build()
        expect:
        timer.hasSame(schedule)
    }

    def "Should hasSame return false if one of the schedules is not same logically"() {
        given:
        def schedule = TimerSchedule.builder()
                .enabled(false)
                .time(of(8, 0))
                .days([MONDAY] as Set)
                .build()
        expect:
        !timer.hasSame(schedule)
    }

    def "Should hasOverlapping return true if one of the schedules has a day in common"() {
        given:
        def schedule = TimerSchedule.builder()
                .enabled(false)
                .time(of(10, 0))
                .days([SATURDAY] as Set)
                .build()
        expect:
        timer.hasOverlapping(schedule)
    }

    def "Should hasOverlapping return false if one of the schedules has a day in common"() {
        given:
        def schedule = TimerSchedule.builder()
                .enabled(false)
                .time(of(8, 0))
                .days([WEDNESDAY] as Set)
                .build()
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
