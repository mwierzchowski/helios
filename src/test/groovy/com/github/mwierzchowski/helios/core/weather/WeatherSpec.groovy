package com.github.mwierzchowski.helios.core.weather

import spock.lang.Specification

import javax.validation.Validation
import java.time.Instant

import static SpeedUnit.KilometersPerHour
import static TemperatureUnit.Celsius

class WeatherSpec extends Specification {
    def timestamp = Instant.now()
    def temperature = new Temperature(20, Celsius)
    def wind = new Wind(new Speed(100, KilometersPerHour), 125)
    def validator = Validation.buildDefaultValidatorFactory().getValidator()

    def "Weather could be created from valid values"() {
        given:
        def weather = new Weather(timestamp, temperature, wind, 100)
        when:
        def errors = validator.validate(weather)
        then:
        errors.isEmpty()
    }

    def "Timestamp could not be null"() {
        given:
        def weather = new Weather(null, temperature, wind, 100)
        when:
        def errors = validator.validate(weather)
        then:
        errors.size() == 1
    }

    def "Timestamp could not be in the future"() {
        given:
        timestamp = Instant.now().plusSeconds(10)
        def weather = new Weather(timestamp, temperature, wind, 100)
        when:
        def errors = validator.validate(weather)
        then:
        errors.size() == 1
    }

    def "Temperature could not be null"() {
        given:
        def weather = new Weather(timestamp, null, wind, 100)
        when:
        def errors = validator.validate(weather)
        then:
        errors.size() == 1
    }

    def "Temperature could not be incorrect"() {
        given:
        temperature.unit = null
        def weather = new Weather(timestamp, temperature, wind, 100)
        when:
        def errors = validator.validate(weather)
        then:
        errors.size() == 1
    }

    def "Wind could not be null"() {
        given:
        def weather = new Weather(timestamp, temperature, null, 100)
        when:
        def errors = validator.validate(weather)
        then:
        errors.size() == 1
    }

    def "Wind could not be incorrect"() {
        given:
        wind.direction = 500
        def weather = new Weather(timestamp, temperature, wind, 100)
        when:
        def errors = validator.validate(weather)
        then:
        errors.size() == 1
    }

    def "Clouds coverage could not be null"() {
        given:
        def weather = new Weather(timestamp, temperature, wind, null)
        when:
        def errors = validator.validate(weather)
        then:
        errors.size() == 1
    }

    def "Clouds coverage could not be less then 0"() {
        given:
        def weather = new Weather(timestamp, temperature, wind, -1)
        when:
        def errors = validator.validate(weather)
        then:
        errors.size() == 1
    }

    def "Clouds coverage could not be bigger then 100"() {
        given:
        def weather = new Weather(timestamp, temperature, wind, 150)
        when:
        def errors = validator.validate(weather)
        then:
        errors.size() == 1
    }
}
