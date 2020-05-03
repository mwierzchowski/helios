package com.github.mwierzchowski.helios.core.weather

import com.github.mwierzchowski.helios.core.commons.EventStore
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
    WeatherPublisher weatherPublisher = new WeatherPublisher(weatherProperties, weatherProvider, eventStore)

    def "Publisher sends weather notification when first observation is available"() {
        given:
        def start = now()
        def weather = weather()
        weatherProvider.currentWeather() >> of(weather)
        when:
        weatherPublisher.publishWeather()
        then:
        1 * eventStore.publish({
            verifyAll(it, WeatherObservationEvent) {
                currentWeather == weather
            }
        })
    }

    def "Publisher sends weather notification when next observation is different then previous one"() {
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

    def "Publisher does not send notification when next observation is the same as previous one"() {
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

    def "Publisher sends warning notification when first observation is missing"() {
        given:
        weatherProvider.currentWeather() >> empty()
        when:
        weatherPublisher.publishWeather()
        then:
        1 * eventStore.publish(_ as WeatherMissingEvent)
    }

    def "Publisher does not send next warning notification when previous was sent"() {
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

    def "Publisher sends warning notification when observations are missing for a long time"() {
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

    def "Publisher does not send warning notification when observations are missing for a short time"() {
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

    def "Publisher always sends weather notification when observation is back after warning was sent"() {
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
        return Weather.builder()
            .timestamp(timestamp)
            .temperature(new Temperature(new BigDecimal(20), CELSIUS))
            .wind(new Wind(new Speed(new BigDecimal(5), KILOMETERS_PER_HOUR), 270))
            .cloudsCoverage(clouds)
            .build()
    }
}
