package com.github.mwierzchowski.helios.core.timers

import com.github.mwierzchowski.helios.core.commons.EventStore
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate
import java.time.LocalTime

import static java.util.Optional.empty

class TimerAlertStarterSpec extends Specification {
    TimerRepository timerRepository = Mock(TimerRepository)
    TaskScheduler taskScheduler = new ConcurrentTaskScheduler()
    EventStore eventStore = Mock(EventStore)

    @Subject
    TimerAlertPublisher alertPublisher = new TimerAlertPublisher(timerRepository, taskScheduler, eventStore)

    def "Should publish alert if timer is scheduled later today"() {
        given:
        def delay = 1
        def timer = timerOf(1, true, delay)
        timerRepository.findById(timer.id) >> Optional.of(timer)
        when:
        alertPublisher.startAlertFor(timer)
        sleepSeconds(delay)
        then:
        1 * eventStore.publish({
            verifyAll(it, TimerAlertEvent) {
                it.timer == timer
            }
        })
    }

    def "Should not publish alert if timer is scheduled for the other day"() {
        given:
        def delay = 1
        def timer = timerOf(1, false, delay)
        when:
        alertPublisher.startAlertFor(timer)
        sleepSeconds(delay)
        then:
        0 * eventStore.publish(_ as TimerAlertEvent)
    }

    def "Should not publish alert before schedule time"() {
        given:
        def timer = timerOf(1, true, 3600)
        when:
        alertPublisher.startAlertFor(timer)
        sleepSeconds(1)
        then:
        0 * eventStore.publish(_ as TimerAlertEvent)
    }

    def "Should not publish alert if timer was removed"() {
        given:
        def delay = 1
        def timerId = 1
        def timer = timerOf(timerId, true, delay)
        timerRepository.findById(timerId) >> empty()
        when:
        alertPublisher.startAlertFor(timer)
        sleepSeconds(delay)
        then:
        0 * eventStore.publish(_ as TimerAlertEvent)
    }

    def "Should not publish alert if schedule was removed"() {
        given:
        def delay = 1
        def timerId = 1
        def timerV1 = timerOf(timerId, true, delay)
        def timerV2 = timerOf(timerId, true, delay).tap {
            it.schedules.clear()
        }
        timerRepository.findById(timerId) >> Optional.of(timerV2)
        when:
        alertPublisher.startAlertFor(timerV1)
        sleepSeconds(delay)
        then:
        0 * eventStore.publish(_ as TimerAlertEvent)
    }

    /** Helper methods ************************************************************************************************/

    def timerOf(id = 1, today = false, delay = -3600, version = 1) {
        new Timer().tap {
            it.id = id
            it.description = "Test timer ${id}"
            it.version = version
            it.add new TimerSchedule().tap {
                it.id = 1
                it.days.add dayOfWeek(today)
                it.time = time(delay)
                it.version = 1
            }
        }
    }

    def time(delay = 0) {
        return LocalTime.now().plusSeconds(delay)
    }

    def dayOfWeek(today = true, difference = -1) {
        def days = today ? 0 : difference
        return LocalDate.now().plusDays(days).getDayOfWeek()
    }

    def sleepSeconds(seconds) {
        sleep(seconds * 1000 as Long)
    }
}
