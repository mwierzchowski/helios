package com.github.mwierzchowski.helios.core.commons

import com.github.mwierzchowski.helios.LightIntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification
import spock.lang.Subject

@LightIntegrationSpec(FixedLocationProvider)
class FixedLocationProviderSpec extends Specification {
    @Subject
    @Autowired
    FixedLocationProvider locationProvider

    def "Should return fixed location"() {
        when:
        def location = locationProvider.locate()
        then:
        location.city == 'Warsaw'
        location.latitude == 52.23
        location.longitude == 21.01
    }
}
