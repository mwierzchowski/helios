package com.github.mwierzchowski.helios.core.weather

import spock.lang.Specification

class WeatherInfoContributorSpec extends Specification {
    def contributor = new WeatherInfoContributor()

    def "Should record weather on observation"() {
        given:
        def weather = new Weather()
        when:
        contributor.onWeatherObservation(new WeatherObservationEvent(weather))
        then:
        contributor.getCurrentWeather() == weather
    }

    def "should clear record on stale observatio"() {
        given:
        def weather = new Weather()
        when:
        contributor.onWeatherStale(new WeatherStaleEvent(weather))
        then:
        contributor.getCurrentWeather() == null
    }
}
