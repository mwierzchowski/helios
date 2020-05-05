package com.github.mwierzchowski.helios.core.weather

import spock.lang.Specification

import javax.validation.Validation
import java.time.Instant

import static SpeedUnit.KILOMETERS_PER_HOUR
import static TemperatureUnit.CELSIUS

class WeatherSpec extends Specification {
    def timestamp = Instant.now()
    def temperature = new Temperature(20, CELSIUS)
    def wind = new Wind(new Speed(100, KILOMETERS_PER_HOUR), 125)
    def validator = Validation.buildDefaultValidatorFactory().getValidator()

    def "Should pass validation if values are correct"() {
        given:
        def weather = weatherOf(timestamp, temperature, wind, 100)
        when:
        def errors = validator.validate(weather)
        then:
        errors.isEmpty()
    }

    def "Should have at least 1 source"() {
        given:
        def weather = weatherOf(timestamp, temperature, wind, 100).tap {
            sources.clear()
        }
        when:
        def errors = validator.validate(weather)
        then:
        errors.size() == 1
    }

    def "Should have not null timestamp"() {
        given:
        def weather = weatherOf(null, temperature, wind, 100)
        when:
        def errors = validator.validate(weather)
        then:
        errors.size() == 1
    }

    def "Should have timestamp in the past"() {
        given:
        timestamp = Instant.now().plusSeconds(10)
        def weather = weatherOf(timestamp, temperature, wind, 100)
        when:
        def errors = validator.validate(weather)
        then:
        errors.size() == 1
    }

    def "Should have not null temperature"() {
        given:
        def weather = weatherOf(timestamp, null, wind, 100)
        when:
        def errors = validator.validate(weather)
        then:
        errors.size() == 1
    }

    def "Should have valid temperature"() {
        given:
        temperature.unit = null
        def weather = weatherOf(timestamp, temperature, wind, 100)
        when:
        def errors = validator.validate(weather)
        then:
        errors.size() == 1
    }

    def "Should have not null wind"() {
        given:
        def weather = weatherOf(timestamp, temperature, null, 100)
        when:
        def errors = validator.validate(weather)
        then:
        errors.size() == 1
    }

    def "Should have valid wind"() {
        given:
        wind.direction = 500
        def weather = weatherOf(timestamp, temperature, wind, 100)
        when:
        def errors = validator.validate(weather)
        then:
        errors.size() == 1
    }

    def "Should have not null clouds"() {
        given:
        def weather = weatherOf(timestamp, temperature, wind, null)
        when:
        def errors = validator.validate(weather)
        then:
        errors.size() == 1
    }

    def "Should have clouds coverage bigger or equal then 0"() {
        given:
        def weather = weatherOf(timestamp, temperature, wind, -1)
        when:
        def errors = validator.validate(weather)
        then:
        errors.size() == 1
    }

    def "Should have clouds coverage lesser or equat then 100"() {
        given:
        def weather = weatherOf(timestamp, temperature, wind, 150)
        when:
        def errors = validator.validate(weather)
        then:
        errors.size() == 1
    }

    def weatherOf(timestamp, temperature, wind, clouds) {
        new Weather().tap {
            it.source = 'Test source'
            it.timestamp = timestamp
            it.temperature = temperature
            it.wind = wind
            it.cloudsCoverage = clouds
        }
    }
}
