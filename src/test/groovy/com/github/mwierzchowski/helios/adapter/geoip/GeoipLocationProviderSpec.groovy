package com.github.mwierzchowski.helios.adapter.geoip

import com.github.mwierzchowski.helios.LightIntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification
import spock.lang.Subject

@LightIntegrationSpec([GeoipProperties, GeoipLocationProvider])
@TestPropertySource(properties = "helios.location.fixed=false")
class GeoipLocationProviderSpec extends Specification {
    @Subject
    @Autowired
    GeoipLocationProvider locationProvider

    def "Should provide location based on IP"() {
        when:
        def location = locationProvider.locate()
        then:
        location.city == "Mountain View"
        location.latitude.round(0) == 37
        location.longitude.round(0) == -122
    }
}
