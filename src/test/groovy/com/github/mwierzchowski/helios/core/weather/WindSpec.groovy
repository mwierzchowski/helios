package com.github.mwierzchowski.helios.core.weather

import spock.lang.Specification

import javax.validation.Validation

import static SpeedUnit.KilometersPerHour

class WindSpec extends Specification {
    def speed = new Speed(100, KilometersPerHour)
    def validator = Validation.buildDefaultValidatorFactory().getValidator()

    def "Wind could be created from correct values"() {
        given:
        def wind = new Wind(speed, 125)
        when:
        def errors = validator.validate(wind)
        then:
        errors.isEmpty()
    }

    def "Speed can not be null"() {
        given:
        def wind = new Wind(null, 125)
        when:
        def errors = validator.validate(wind)
        then:
        errors.size() == 1
    }

    def "Speed can not be incorrect"() {
        given:
        speed.value = -1
        def wind = new Wind(speed, 125)
        when:
        def errors = validator.validate(wind)
        then:
        errors.size() == 1
    }

    def "Direction can not be null"() {
        given:
        def wind = new Wind(speed, null)
        when:
        def errors = validator.validate(wind)
        then:
        errors.size() == 1
    }

    def "Direction can not be lesser then 0"() {
        given:
        def wind = new Wind(speed, -1)
        when:
        def errors = validator.validate(wind)
        then:
        errors.size() == 1
    }

    def "Direction can not be bigger then 360"() {
        given:
        def wind = new Wind(speed, 360)
        when:
        def errors = validator.validate(wind)
        then:
        errors.size() == 1
    }
}
