package com.github.mwierzchowski.helios.core.rules

import com.github.mwierzchowski.helios.core.timers.Timer
import com.github.mwierzchowski.helios.core.timers.TimerAlertEvent
import com.github.mwierzchowski.helios.core.timers.TimerRemovedEvent
import com.github.mwierzchowski.helios.core.weather.Weather
import com.github.mwierzchowski.helios.core.weather.WeatherStaleEvent
import spock.lang.Specification
import spock.lang.Subject

class SceneRuleExecutorSpec extends Specification {
    def properties = new SceneRuleProperties()

    @Subject
    def executor = new SceneRuleExecutor(properties)

    def "Should update facts on supported events"() {
        given:
        def timer = new Timer()
        def event = new TimerAlertEvent(timer)
        when:
        executor.executeRulesFor(event)
        then:
        // TODO this specification is rather dummy
        noExceptionThrown()
    }

    def "Should reset facts on stale events"() {
        given:
        def weather = new Weather()
        def event = new WeatherStaleEvent(weather)
        when:
        executor.executeRulesFor(event)
        then:
        // TODO this specification is rather dummy
        noExceptionThrown()
    }

    def "Should do nothing on not supported events"() {
        given:
        def timer = new Timer()
        def event = new TimerRemovedEvent(timer)
        when:
        executor.executeRulesFor(event)
        then:
        // TODO this specification is rather dummy
        noExceptionThrown()
    }
}
