package com.github.mwierzchowski.helios.adapter.owm

import com.github.mwierzchowski.helios.IntegrationSpec
import com.github.tomakehurst.wiremock.matching.UrlPattern
import org.openweathermap.model.CurrentWeatherResponse
import org.spockframework.spring.SpringSpy
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification
import spock.lang.Subject

import static com.github.tomakehurst.wiremock.client.WireMock.*

@IntegrationSpec
class OwmWeatherProviderSpec extends Specification {
    @Subject
    @Autowired
    OwmWeatherProvider weatherProvider

    @SpringSpy
    OwmHealthIndicator healthIndicator

    UrlPattern weatherUrl = urlMatching("/data/2.5/weather.*")

    def setup() {
        weatherProvider.expireCachedResponse()
    }

    def "Provider gives current conditions"() {
        expect:
        weatherProvider.currentWeather().get() != null
    }

    def "Provider gives cached conditions on next attempt"() {
        given:
        def weather1 = weatherProvider.currentWeather().get()
        when:
        def weather2 = weatherProvider.currentWeather().get()
        then:
        weather1.timestamp == weather2.timestamp
        verify(1, getRequestedFor(weatherUrl))
    }

    def "Provider cache could be expired"() {
        given:
        weatherProvider.currentWeather()
        when:
        weatherProvider.expireCachedResponse()
        weatherProvider.currentWeather()
        then:
        verify(2, getRequestedFor(weatherUrl))
    }

    def "Provider registers successful requests in health indicator"() {
        when:
        weatherProvider.currentWeather()
        then:
        1 * healthIndicator.register(_ as CurrentWeatherResponse)
    }

    def "Provider retries failed requests, registers failure in health indicator and gives empty optional"() {
        given:
        stubFor(get(weatherUrl).willReturn(aResponse().withStatus(400)))
        when:
        def optionalWeather = weatherProvider.currentWeather()
        then:
        optionalWeather.isEmpty()
        verify(3, getRequestedFor(weatherUrl))
        1 * healthIndicator.register(_ as Throwable)
    }
}
