package com.github.mwierzchowski.helios.core.weather

import spock.lang.Specification

class TemperatureUnitSpec extends Specification {
    def "Temperature unit could be C (Celsius)"() {
        given:
        char unitSymbol = 'C'
        when:
        TemperatureUnit tu = TemperatureUnit.ofSymbol(unitSymbol)
        then:
        tu.symbol == unitSymbol
    }

    def "Temperature unit could be F (Fahrenheit)"() {
        given:
        char unitSymbol = 'F'
        when:
        TemperatureUnit tu = TemperatureUnit.ofSymbol(unitSymbol)
        then:
        tu.symbol == unitSymbol
    }

    def "Temperature unit could be K (Kelvin)"() {
        given:
        char unitSymbol = 'K'
        when:
        TemperatureUnit tu = TemperatureUnit.ofSymbol(unitSymbol)
        then:
        tu.symbol == unitSymbol
    }

    def "Exception is thrown when unit symbol is not upper case"() {
        given:
        char unitSymbol = 'c'
        when:
        TemperatureUnit.ofSymbol(unitSymbol)
        then:
        thrown IllegalArgumentException
    }

    def "Exception is thrown when temperature unit is not supported"() {
        given:
        char unitSymbol = 'X'
        when:
        TemperatureUnit.ofSymbol(unitSymbol)
        then:
        thrown IllegalArgumentException
    }
}