package com.github.mwierzchowski.helios.adapter.owm

import com.github.mwierzchowski.helios.LiteIntegrationSpec
import com.github.mwierzchowski.helios.adapter.commons.ExternalServiceHealthIndicator
import com.github.tomakehurst.wiremock.matching.UrlPattern
import org.openweathermap.model.CurrentWeatherResponse
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification
import spock.lang.Subject

import static com.github.tomakehurst.wiremock.client.WireMock.*

@LiteIntegrationSpec([OwmConfiguration, OwmProperties, OwmWeatherProvider])
class OwmWeatherProviderSpec extends Specification {
    @Subject
    @Autowired
    OwmWeatherProvider weatherProvider

    @SpringBean
    ExternalServiceHealthIndicator<CurrentWeatherResponse> healthIndicator = Mock()

    UrlPattern weatherUrl = urlMatching("/owm-mock/.*")

    def setup() {
        weatherProvider.expireCachedResponse()
    }

    def "Should return current conditions"() {
        when:
        def weather = weatherProvider.currentWeather().get()
        then:
        weather.isProvided()
        weather.sources[0] == OwmWeatherProvider.WEATHER_SOURCE_NAME
    }

    def "Should return cached conditions on next attempt"() {
        given:
        def weather1 = weatherProvider.currentWeather().get()
        when:
        def weather2 = weatherProvider.currentWeather().get()
        then:
        weather1.timestamp == weather2.timestamp
        verify(1, getRequestedFor(weatherUrl))
    }

    def "Should expire cache"() {
        given:
        weatherProvider.currentWeather()
        when:
        weatherProvider.expireCachedResponse()
        weatherProvider.currentWeather()
        then:
        verify(2, getRequestedFor(weatherUrl))
    }

    def "Should register successful requests in health indicator"() {
        when:
        weatherProvider.currentWeather()
        then:
        1 * healthIndicator.register(_ as CurrentWeatherResponse)
    }

    def "Should retry failed requests, register failure in health indicator and give empty optional"() {
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
