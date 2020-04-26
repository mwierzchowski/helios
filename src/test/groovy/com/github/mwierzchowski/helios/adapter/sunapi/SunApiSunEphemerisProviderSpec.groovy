package com.github.mwierzchowski.helios.adapter.sunapi

import com.github.mwierzchowski.helios.LightIntegrationSpec
import com.github.tomakehurst.wiremock.matching.UrlPattern
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.sunrisesunset.model.SunriseSunsetResponse
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate
import java.time.LocalTime

import static com.github.mwierzchowski.helios.core.sun.SunEphemerisEventType.Dawn
import static com.github.tomakehurst.wiremock.client.WireMock.*

@LightIntegrationSpec([SunApiConfiguration, SunApiProperties, SunApiSunEphemerisProvider])
class SunApiSunEphemerisProviderSpec extends Specification {
    @Subject
    @Autowired
    SunApiSunEphemerisProvider ephemerisProvider

    @Autowired
    SunApiProperties sunProperties

    @SpringBean
    SunApiHealthIndicator healthIndicator = Mock()

    UrlPattern sunapiUrl = urlPathMatching("/sun-mock/.*")

    def setup() {
        ephemerisProvider.cache.clear()
    }

    def "Should provide given day sun ephemeris"() {
        given:
        def today = LocalDate.now()
        when:
        def ephemeris = ephemerisProvider.sunEphemerisFor(today)
        then:
        with (ephemeris) {
            day != null
            times.size() == 5
            !approximated
        }
        1 * healthIndicator.register(_ as SunriseSunsetResponse)
        verify(1, getRequestedFor(sunapiUrl))
    }

    def "Should provide cached ephemeris on next attempt"() {
        given:
        def today = LocalDate.now()
        def ephemeris1 = ephemerisProvider.sunEphemerisFor(today)
        when:
        def ephemeris2 = ephemerisProvider.sunEphemerisFor(today)
        then:
        ephemeris1 == ephemeris2
        verify(1, getRequestedFor(sunapiUrl))
    }

    def "Should cache ephemeris on demand"() {
        given:
        def today = LocalDate.now()
        when:
        ephemerisProvider.manageCache()
        sleep(500)
        then:
        ephemerisProvider.cache.size() == sunProperties.cacheDays
        ephemerisProvider.cache.containsKey(today)
        ephemerisProvider.cache.containsKey(today.plusDays(sunProperties.cacheDays - 1))
        sunProperties.cacheDays * healthIndicator.register(_ as SunriseSunsetResponse)
        verify(sunProperties.cacheDays, getRequestedFor(sunapiUrl))
    }

    def "Should not cache past days ephemeris"() {
        given:
        def today = LocalDate.now()
        def weekAgo = today.minusDays(7)
        ephemerisProvider.sunEphemerisFor(today)
        ephemerisProvider.sunEphemerisFor(weekAgo)
        when:
        ephemerisProvider.manageCache()
        then:
        ephemerisProvider.cache.containsKey(today)
        !ephemerisProvider.cache.containsKey(weekAgo)
    }

    def "Should retry on failed request and provide non cacheable fallback"() {
        given:
        def today = LocalDate.now()
        stubFor(get(sunapiUrl).willReturn(aResponse().withStatus(400)))
        when:
        def fallback = ephemerisProvider.sunEphemerisFor(today)
        then:
        fallback.day == today
        fallback.times.get(Dawn) == LocalTime.parse(sunProperties.fallback.dawn)
        fallback.approximated
        ephemerisProvider.cache.size() == 0
        1 * healthIndicator.register(_ as Throwable)
        verify(3, getRequestedFor(sunapiUrl))
    }
}
