package com.github.mwierzchowski.helios.core.weather

import spock.lang.Specification

import javax.validation.Validation

class TemperatureSpec extends Specification {
    def validator = Validation.buildDefaultValidatorFactory().getValidator()

    def "Temperature could be created when values are correct"() {
        given:
        def temperature = new Temperature(0, TemperatureUnit.CELSIUS)
        when:
        def errors = validator.validate(temperature)
        then:
        errors.isEmpty()
    }

    def "Temperature's value can not be null"() {
        given:
        def temperature = new Temperature(null, TemperatureUnit.CELSIUS)
        when:
        def errors = validator.validate(temperature)
        then:
        errors.size() == 1
    }

    def "Temerature's unit can not be null"() {
        given:
        def temperature = new Temperature(0, null)
        when:
        def errors = validator.validate(temperature)
        then:
        errors.size() == 1
    }
}
