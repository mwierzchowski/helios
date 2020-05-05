package com.github.mwierzchowski.helios.core.weather

import com.github.mwierzchowski.helios.core.commons.EventStore
import com.github.mwierzchowski.helios.core.commons.HeliosEvent
import spock.lang.Specification
import spock.lang.Subject

import static com.github.mwierzchowski.helios.core.weather.SpeedUnit.KILOMETERS_PER_HOUR
import static com.github.mwierzchowski.helios.core.weather.TemperatureUnit.CELSIUS
import static java.time.Instant.now
import static java.util.Optional.empty
import static java.util.Optional.of

class WeatherPublisherSpec extends Specification {
    WeatherProperties weatherProperties = new WeatherProperties()
    WeatherProvider weatherProvider = Mock()
    EventStore eventStore = Mock()

    @Subject
    WeatherPublisher weatherPublisher = new WeatherPublisher(weatherProperties, [weatherProvider], eventStore)

    def "Should do nothing if weather providers are not available"() {
        given:
        weatherPublisher = new WeatherPublisher(weatherProperties, null, eventStore)
        when:
        weatherPublisher.publishWeather()
        then:
        0 * eventStore.publish(_ as HeliosEvent)
    }

    def "Should merge observation if more then 1 weather provider is available"() {
        given:
        def temp = 20
        def wind = 5
        def weather1 = new Weather().tap {
            it.source = 'Test source 1'
            it.timestamp = now()
            it.temperature = new Temperature(new BigDecimal(temp), CELSIUS)
        }
        def weather2 = new Weather().tap {
            it.source = 'Test source 2'
            it.timestamp = now()
            it.wind = new Wind(new Speed(new BigDecimal(5), KILOMETERS_PER_HOUR), 270)
        }
        def provider1 = Mock(WeatherProvider)
        def provider2 = Mock(WeatherProvider)
        provider1.currentWeather() >> of(weather1)
        provider2.currentWeather() >> of(weather2)
        weatherPublisher = new WeatherPublisher(weatherProperties, [provider1, provider2], eventStore)
        when:
        weatherPublisher.publishWeather()
        then:
        1 * eventStore.publish({
            verifyAll(it, WeatherObservationEvent) {
                it.currentWeather.temperature.value == temp
                it.currentWeather.wind.speed.value == wind
            }
        })
    }

    def "Should send weather notification if first observation is available"() {
        given:
        def weather = weather()
        weatherProvider.currentWeather() >> of(weather)
        when:
        weatherPublisher.publishWeather()
        then:
        1 * eventStore.publish({
            verifyAll(it, WeatherObservationEvent) {
                currentWeather.isSameAs(weather)
            }
        })
    }

    def "Should send weather notification if next observation is different then previous one"() {
        given:
        def weather1 = of(weather(now()))
        def weather2 = of(weather(now().plusSeconds(100), 50))
        weatherProvider.currentWeather() >>> [weather1, weather2]
        when:
        2.times {
            weatherPublisher.publishWeather()
        }
        then:
        2 * eventStore.publish(_ as WeatherObservationEvent)
        0 * eventStore.publish(_ as WeatherMissingEvent)
    }

    def "Should not send notification if next observation is the same as previous one"() {
        given:
        def weather1 = of(weather(now()))
        def weather2 = of(weather(now().plusSeconds(100)))
        weatherProvider.currentWeather() >>> [weather1, weather2]
        when:
        2.times {
            weatherPublisher.publishWeather()
        }
        then:
        1 * eventStore.publish(_ as WeatherObservationEvent)
        0 * eventStore.publish(_ as WeatherMissingEvent)
    }

    def "Should send warning notification if first observation is missing"() {
        given:
        weatherProvider.currentWeather() >> empty()
        when:
        weatherPublisher.publishWeather()
        then:
        1 * eventStore.publish(_ as WeatherMissingEvent)
    }

    def "Should not send next warning notification if previous was sent"() {
        given:
        def weather1 = empty()
        def weather2 = empty()
        weatherProvider.currentWeather() >>> [weather1, weather2]
        when:
        2.times {
            weatherPublisher.publishWeather()
        }
        then:
        1 * eventStore.publish(_ as WeatherMissingEvent)
        0 * eventStore.publish(_ as WeatherObservationEvent)
    }

    def "Should send warning notification if observations are missing for a long time"() {
        given:
        def weather1 = of(weather(now().minusMillis(weatherProperties.observationDeadline + 10000)))
        def weather2 = empty()
        weatherProvider.currentWeather() >>> [weather1, weather2]
        when:
        2.times {
            weatherPublisher.publishWeather()
        }
        then:
        1 * eventStore.publish(_ as WeatherObservationEvent)
        1 * eventStore.publish(_ as WeatherMissingEvent)
    }

    def "Should not send warning notification if observations are missing for a short time"() {
        given:
        def weather1 = of(weather(now().minusMillis(weatherProperties.observationDeadline - 10000)))
        def weather2 = empty()
        weatherProvider.currentWeather() >>> [weather1, weather2]
        when:
        2.times {
            weatherPublisher.publishWeather()
        }
        then:
        1 * eventStore.publish(_ as WeatherObservationEvent)
        0 * eventStore.publish(_ as WeatherMissingEvent)
    }

    def "Should always sends weather notification if observation is back after warning was sent"() {
        given:
        def weather1 = of(weather(now().minusMillis(weatherProperties.observationDeadline + 10000)))
        def weather2 = empty()
        def weather3 = of(weather(now()))
        weatherProvider.currentWeather() >>> [weather1, weather2, weather3]
        when:
        3.times {
            weatherPublisher.publishWeather()
        }
        then:
        2 * eventStore.publish(_ as WeatherObservationEvent)
        1 * eventStore.publish(_ as WeatherMissingEvent)
    }

    def weather(timestamp = now(), clouds = 0) {
        new Weather().tap {
            it.source = 'Test source'
            it.timestamp = timestamp
            it.temperature = new Temperature(new BigDecimal(20), CELSIUS)
            it.wind = new Wind(new Speed(new BigDecimal(5), KILOMETERS_PER_HOUR), 270)
            it.cloudsCoverage = clouds
        }
    }
}
