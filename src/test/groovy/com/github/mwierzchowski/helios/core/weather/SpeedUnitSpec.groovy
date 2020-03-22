package com.github.mwierzchowski.helios.core.weather

import spock.lang.Specification

class SpeedUnitSpec extends Specification {
    def "Speed could be kilometers per hour"() {
        given:
        def symbol = "km/h"
        when:
        SpeedUnit su = SpeedUnit.ofSymbol(symbol)
        then:
        su.symbol == symbol
    }

    def "Speed could be meters per second"() {
        given:
        def symbol = "m/s"
        when:
        SpeedUnit su = SpeedUnit.ofSymbol(symbol)
        then:
        su.symbol == symbol
    }

    def "Speed could be miles per hour"() {
        given:
        def symbol = "mph"
        when:
        SpeedUnit su = SpeedUnit.ofSymbol(symbol)
        then:
        su.symbol == symbol
    }

    def "Exception is thrown when unit is not supported"() {
        given:
        def symbol = "x"
        when:
        SpeedUnit.ofSymbol(symbol)
        then:
        thrown IllegalArgumentException
    }
}
