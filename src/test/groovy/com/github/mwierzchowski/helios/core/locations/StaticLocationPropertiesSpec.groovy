package com.github.mwierzchowski.helios.core.locations

import com.github.mwierzchowski.helios.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification
import spock.lang.Subject

@IntegrationSpec
class StaticLocationPropertiesSpec extends Specification {
    @Subject
    @Autowired
    StaticLocationProperties locationProperties

    def "Should return location"() {
        when:
        def location = locationProperties.locate()
        then:
        location.city == 'Warsaw'
        location.latitude == 52.23
        location.longitude == 21.01
    }
}
