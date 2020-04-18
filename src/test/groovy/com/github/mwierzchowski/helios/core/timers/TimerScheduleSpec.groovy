package com.github.mwierzchowski.helios.core.timers

import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime

class TimerScheduleSpec extends Specification {

    def "Should be same if other schedule has same time related data"() {
        given:
        def daysDiff = 1
        def hoursDiff = 2
        def now = LocalDateTime.now()
        def schedule1 = new TimerSchedule().tap {
            id = 1
            version = 1
            it.days = [now.toLocalDate().plusDays(daysDiff).getDayOfWeek()]
            it.time = now.toLocalTime().plusHours(hoursDiff)
        }
        def schedule2 = new TimerSchedule().tap {
            id = 2
            version = 2
            it.days = [now.toLocalDate().plusDays(daysDiff).getDayOfWeek()]
            it.time = now.toLocalTime().plusHours(hoursDiff)
        }
        expect:
        schedule1.isSame(schedule2)
    }

    def "Should be not same if other schedule is null"() {
        given:
        def schedule1 = timerSchedule()
        def schedule2 = null
        expect:
        !schedule1.isSame(schedule2)
    }

    def "Should be not same if other schedule has different time related data"() {
        given:
        def now = LocalDateTime.now()
        def schedule1 = new TimerSchedule().tap {
            it.days = [now.toLocalDate().plusDays(1).getDayOfWeek()]
            it.time = now.toLocalTime().plusHours(1)
        }
        def schedule2 = new TimerSchedule().tap {
            it.days = [now.toLocalDate().plusDays(2).getDayOfWeek()]
            it.time = now.toLocalTime().plusHours(2)
        }
        expect:
        !schedule1.isSame(schedule2)
    }

    def "Should be overlapping if schedules have the same day"() {
        given:
        def schedule1 = new TimerSchedule().tap {
            id = 1
            days.add LocalDate.now().getDayOfWeek()
        }
        def schedule2 = new TimerSchedule().tap {
            id = 2
            days.add LocalDate.now().getDayOfWeek()
        }
        expect:
        schedule1.isOverlapping(schedule2)
        schedule2.isOverlapping(schedule1)
    }

    def "Should be overlapping if schedules have at least 1 same day"() {
        given:
        def schedule1 = new TimerSchedule().tap {
            id = 1
            days.add LocalDate.now().getDayOfWeek()
            days.add LocalDate.now().plusDays(1).getDayOfWeek()
        }
        def schedule2 = new TimerSchedule().tap {
            id = 2
            days.add LocalDate.now().getDayOfWeek()
        }
        expect:
        schedule1.isOverlapping(schedule2)
        schedule2.isOverlapping(schedule1)
    }

    def "Should not be overlapping if schedules have no days in common"() {
        given:
        def schedule1 = new TimerSchedule().tap {
            id = 1
            days.add LocalDate.now().getDayOfWeek()
            days.add LocalDate.now().plusDays(1).getDayOfWeek()
        }
        def schedule2 = new TimerSchedule().tap {
            id = 2
            days.add LocalDate.now().plusDays(-1).getDayOfWeek()
        }
        expect:
        !schedule1.isOverlapping(schedule2)
        !schedule2.isOverlapping(schedule1)
    }

    def "Should nearest occurrence return next week if schedule was yesterday"() {
        given:
        def schedule = timerSchedule(-1)
        when:
        def occurrence = schedule.nearestOccurrence()
        then:
        pointInTime(6).isAfter(occurrence)
    }

    def "Should nearest occurrence return next week if schedule was earlier today"() {
        given:
        def schedule = timerSchedule(0, -1)
        when:
        def occurrence = schedule.nearestOccurrence()
        then:
        pointInTime(7, -1).isAfter(occurrence)
    }

    def "Should nearest occurrence return today if schedule is later today"() {
        given:
        def schedule = timerSchedule(0, 1)
        when:
        def occurrence = schedule.nearestOccurrence()
        then:
        pointInTime(0, 1).isAfter(occurrence)
    }

    def "Should nearest occurrence return tomorrow if schedule is tomorrow"() {
        given:
        def schedule = timerSchedule(1)
        when:
        def occurrence = schedule.nearestOccurrence()
        then:
        pointInTime(1, 0).isAfter(occurrence)
    }

    def "Should nearest occurrence return tomorrow if schedule is yesterday and tomorrow"() {
        given:
        def schedule = timerSchedule(1).tap {
            days.add LocalDate.now().plusDays(-1).getDayOfWeek()
        }
        when:
        def occurrence = schedule.nearestOccurrence()
        then:
        pointInTime(1, 0).isAfter(occurrence)
    }

    def "Should nearest occurrence return next week if schedule is yesterday and earlier today"() {
        given:
        def schedule = timerSchedule(0, -60).tap {
            days.add LocalDate.now().plusDays(-1).getDayOfWeek()
        }
        when:
        def occurrence = schedule.nearestOccurrence()
        then:
        pointInTime(6, -1).isAfter(occurrence)
    }

    /** Helper methods ************************************************************************************************/

    def timerSchedule(daysDiff = 0, hoursDiff = 0) {
        new TimerSchedule().tap {
            it.days = [LocalDate.now().plusDays(daysDiff).getDayOfWeek()]
            it.time = LocalTime.now().plusHours(hoursDiff)
        }
    }

    def pointInTime(days = 0, hours = 0) {
        ZonedDateTime.now().plusDays(days).plusHours(hours).toInstant()
    }
}
