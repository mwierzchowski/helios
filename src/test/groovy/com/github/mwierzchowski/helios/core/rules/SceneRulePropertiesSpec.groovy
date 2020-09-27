package com.github.mwierzchowski.helios.core.rules

import com.github.mwierzchowski.helios.core.sun.SunEphemerisEvent
import com.github.mwierzchowski.helios.core.timers.TimerAlertEvent
import com.github.mwierzchowski.helios.core.weather.WeatherObservationEvent
import com.github.mwierzchowski.helios.core.weather.WeatherStaleEvent
import spock.lang.Specification
import spock.lang.Subject

class SceneRulePropertiesSpec extends Specification {
    @Subject
    def properties = new SceneRuleProperties()

    def "Should contain fact events"() {
        expect:
        properties.factEvents.containsAll([WeatherObservationEvent, SunEphemerisEvent, TimerAlertEvent])
    }

    def "Should contain resent events"() {
        expect:
        properties.resetEvents.containsAll([WeatherStaleEvent])
    }
}
