package com.github.mwierzchowski.helios.core.weather

import spock.lang.Specification

import javax.validation.Validation

import static SpeedUnit.KilometersPerHour

class SpeedSpec extends Specification {
    def validator = Validation.buildDefaultValidatorFactory().getValidator()

    def "Speed could be created when values are correct"() {
        given:
        def speed = new Speed(0, KilometersPerHour)
        when:
        def errors = validator.validate(speed)
        then:
        errors.isEmpty()
    }

    def "Speed can not have null value"() {
        given:
        def speed = new Speed(null, KilometersPerHour)
        when:
        def errors = validator.validate(speed)
        then:
        errors.size() == 1
    }

    def "Speed can not have value lesser then 0"() {
        given:
        def speed = new Speed(-1, KilometersPerHour)
        when:
        def errors = validator.validate(speed)
        then:
        errors.size() == 1
    }

    def "Speed can not have null unit"() {
        given:
        def speed = new Speed(0, null)
        when:
        def errors = validator.validate(speed)
        then:
        errors.size() == 1
    }
}
