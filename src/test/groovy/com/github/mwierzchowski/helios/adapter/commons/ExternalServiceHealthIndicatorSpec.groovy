package com.github.mwierzchowski.helios.adapter.commons

import spock.lang.Specification
import spock.lang.Subject

import static org.springframework.boot.actuate.health.Status.*

class ExternalServiceHealthIndicatorSpec extends Specification {
    @Subject
    def healthIndicator = new ExternalServiceHealthIndicator<String>()

    def "Health indicator provides UNKNOWN status when request history is empty"() {
        expect:
        healthIndicator.health().status == UNKNOWN
    }

    def "Health indicator provides UP status when last request was a successful"() {
        given:
        healthIndicator.register(new RuntimeException())
        healthIndicator.register("success")
        expect:
        healthIndicator.health().status == UP
    }

    def "Health indicator provides DOWN status when last request was a failure"() {
        given:
        healthIndicator.register("success")
        healthIndicator.register(new RuntimeException())
        expect:
        healthIndicator.health().status == DOWN
    }

    def "Health indicator provides history of max 10 last requests statuses starting from the most recent one"() {
        given:
        def requestsCount = 11
        (requestsCount).times {
            healthIndicator.register("success")
        }
        when:
        def history = healthIndicator.recentHistory
        then:
        history.size() == ExternalServiceHealthIndicator.HISTORY_MAX
        def id = requestsCount - 1
        history.forEach {
            id -= 1
        }
    }

    def "Health indicator provides last successful response"() {
        given:
        def successResponse1 = "success1"
        def successResponse2 = "success2"
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
            healthIndicator.register("success")
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
            healthIndicator.register("success")
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
            healthIndicator.register("success")
        }
        expect:
        healthIndicator.allRequestCounter == requestCounter
    }
}
