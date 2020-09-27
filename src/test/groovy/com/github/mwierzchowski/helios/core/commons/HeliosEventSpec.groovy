package com.github.mwierzchowski.helios.core.commons

import com.github.mwierzchowski.helios.core.weather.WeatherObservationEvent
import spock.lang.Specification
import spock.lang.Subject

class HeliosEventSpec extends Specification {
    @Subject
    def event = new WeatherObservationEvent(null)

    def "Should return null source"() {
        expect:
        event.source == null
    }

    def "Should return null subject"() {
        expect:
        event.subject == null
    }

    def "Should return null timestamp"() {
        expect:
        event.timestamp == null
    }

    def "Should return null zoned timestamo"() {
        expect:
        event.zonedDateTime == null
    }
}
