package com.github.mwierzchowski.helios.core.commons

import com.github.mwierzchowski.helios.LightIntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification
import spock.lang.Subject

@LightIntegrationSpec(FixedLocationProperties)
class FixedLocationPropertiesSpec extends Specification {
    @Subject
    @Autowired
    FixedLocationProperties locationProperties

    def "Should return location"() {
        when:
        def location = locationProperties.locate()
        then:
        location.city == 'Warsaw'
        location.latitude == 52.23
        location.longitude == 21.01
    }
}
