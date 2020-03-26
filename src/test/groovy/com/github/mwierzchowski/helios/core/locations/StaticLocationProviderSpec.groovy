package com.github.mwierzchowski.helios.core.locations

import com.github.mwierzchowski.helios.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification
import spock.lang.Subject

@IntegrationSpec
class StaticLocationProviderSpec extends Specification {
    @Subject
    @Autowired
    StaticLocationProvider locationProvider

    def "Provider returns configured location"() {
        when:
        def location = locationProvider.locate()
        then:
        location.city == 'Warsaw'
        location.latitude == 52.23
        location.longitude == 21.01
    }
}
