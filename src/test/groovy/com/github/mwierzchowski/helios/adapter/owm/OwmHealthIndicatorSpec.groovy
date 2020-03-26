package com.github.mwierzchowski.helios.adapter.owm

import org.openweathermap.model.CurrentWeatherResponse
import spock.lang.Specification
import spock.lang.Subject

import static org.springframework.boot.actuate.health.Status.*

class OwmHealthIndicatorSpec extends Specification {
    @Subject
    def healthIndicator = new OwmHealthIndicator()

    def "Health indicator provides UNKNOWN status when request history is empty"() {
        expect:
        healthIndicator.health().status == UNKNOWN
    }

    def "Health indicator provides UP status when last request was a successful"() {
        given:
        healthIndicator.register(new RuntimeException())
        healthIndicator.register(new CurrentWeatherResponse())
        expect:
        healthIndicator.health().status == UP
    }

    def "Health indicator provides DOWN status when last request was a failure"() {
        given:
        healthIndicator.register(new CurrentWeatherResponse())
        healthIndicator.register(new RuntimeException())
        expect:
        healthIndicator.health().status == DOWN
    }

    def "Health indicator provides history of max 10 last requests statuses starting from the most recent one"() {
        given:
        def requestsCount = 11
        (requestsCount).times {
            healthIndicator.register(new CurrentWeatherResponse().dt(it))
        }
        when:
        def history = healthIndicator.recentHistory
        then:
        history.size() == OwmHealthIndicator.HISTORY_MAX
        def id = requestsCount - 1
        history.forEach {
            assert it.response.getDt() == id
            id -= 1
        }
    }

    def "Health indicator provides last successful response"() {
        given:
        def successResponse1 = new CurrentWeatherResponse().dt(100)
        def successResponse2 = new CurrentWeatherResponse().dt(200)
        healthIndicator.register(successResponse1)
        healthIndicator.register(successResponse2)
        15.times {
            healthIndicator.register(new RuntimeException())
        }
        expect:
        healthIndicator.lastSuccess.response != successResponse1
        healthIndicator.lastSuccess.response == successResponse2
    }

    def "Health indicator provides null as last successful when history is empty"() {
        expect:
        healthIndicator.lastSuccess == null
    }

    def "Health indicator provides last failure reason"() {
        given:
        def failureReason1 = new RuntimeException()
        def failureReason2 = new RuntimeException()
        healthIndicator.register(failureReason1)
        healthIndicator.register(failureReason2)
        15.times {
            healthIndicator.register(new CurrentWeatherResponse())
        }
        expect:
        healthIndicator.lastFailure.throwable != failureReason1
        healthIndicator.lastFailure.throwable == failureReason2
    }

    def "Health indicator provides null as last failure when history is empty"() {
        expect:
        healthIndicator.lastFailure == null
    }

    def "Health indicator provides success/failure statistics since its start"() {
        given:
        def successCount = 95
        def failureCount = 5
        when:
        successCount.times {
            healthIndicator.register(new CurrentWeatherResponse())
        }
        failureCount.times {
            healthIndicator.register(new RuntimeException())
        }
        then:
        healthIndicator.successRate == (double) successCount / (successCount + failureCount) * 100
        healthIndicator.failureRate == (double) failureCount / (successCount + failureCount) * 100
    }

    def "Health indicator provides zero statistics when history is empty"() {
        expect:
        healthIndicator.successRate == 0
        healthIndicator.failureRate == 0
    }

    def "Health indicator provides all requests counter since its start"() {
        given:
        def requestCounter = new Random().nextInt(10)
        requestCounter.times {
            healthIndicator.register(new CurrentWeatherResponse())
        }
        expect:
        healthIndicator.allRequestCounter == requestCounter
    }
}
