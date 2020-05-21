package com.github.mwierzchowski.helios.core.sun

import com.github.mwierzchowski.helios.core.commons.EventStore
import spock.lang.Specification

import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.Executors

import static com.github.mwierzchowski.helios.core.sun.SunEphemerisType.*

class SunEphemerisPublisherSpec extends Specification {
    def ephemerisProvider = Mock(SunEphemerisProvider)
    def executorService = Executors.newSingleThreadScheduledExecutor()
    def eventStore = Mock(EventStore)

    def cleanup() {
        executorService.shutdownNow()
    }

    def "Should publish events that did not pass yet"() {
        given:
        def clock = clockSetTo(11, 59, 59)
        def today = LocalDate.now(clock)
        ephemerisProvider.sunEphemerisFor(today) >> new SunEphemeris().tap {
            it.day = today
            it.times.put DAWN, timeOf(0)
            it.times.put SUNRISE, timeOf(4)
            it.times.put NOON, timeOf(12)
            it.times.put SUNSET, timeOf(20)
            it.times.put DUSK, timeOf(22)
        }
        def ephemerisPublisher = new SunEphemerisPublisher(clock, ephemerisProvider, executorService, eventStore)
        when:
        ephemerisPublisher.startPublishingEvents()
        sleep(1500)
        then:
        1 * eventStore.publish({
            verifyAll(it, SunEphemerisEvent) {
                type == NOON
            }
        })
    }

    def "Should publish events for next day if today all events already passed"() {
        given:
        def clock = clockSetTo(23, 59, 59)
        def today = LocalDate.now(clock)
        ephemerisProvider.sunEphemerisFor(today) >> new SunEphemeris().tap {
            it.day = today
            it.times.put DUSK, timeOf(22)
        }
        ephemerisProvider.sunEphemerisFor(today.plusDays(1)) >> new SunEphemeris().tap {
            it.day = today.plusDays(1)
            it.times.put DAWN, timeOf(0)
            it.times.put SUNRISE, timeOf(4)
        }
        def ephemerisPublisher = new SunEphemerisPublisher(clock, ephemerisProvider, executorService, eventStore)
        when:
        ephemerisPublisher.startPublishingEvents()
        sleep(1500)
        then:
        1 * eventStore.publish({
            verifyAll(it, SunEphemerisEvent) {
                type == DAWN
            }
        })
    }

    // Should publish events with configured offset
    // Should publish events immediately if time did not pass yet but offset passed the deadline

    def clockSetTo(hour, minute, second) {
        def day = LocalDate.now()
        def time = LocalTime.of(hour, minute, second)
        def zone = ZoneId.systemDefault()
        return Clock.fixed(time.atDate(day).atZone(zone).toInstant(), zone)
    }

    def timeOf(hour, minute = 0, second = 0) {
        LocalTime.of(hour, minute, second)
    }
}
