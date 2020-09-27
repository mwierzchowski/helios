package com.github.mwierzchowski.helios.core.rules

import com.github.mwierzchowski.helios.core.timers.Timer
import com.github.mwierzchowski.helios.core.timers.TimerAlertEvent
import com.github.mwierzchowski.helios.core.weather.Weather
import com.github.mwierzchowski.helios.core.weather.WeatherStaleEvent
import spock.lang.Specification

class SceneRuleExecutorSpec extends Specification {
    def properties = new SceneRuleProperties()
    def executor = new SceneRuleExecutor(properties)

    // TODO this specification is rather dummy
    def "Should update facts on supported events"() {
        given:
        def timer = new Timer()
        def event = new TimerAlertEvent(timer)
        when:
        executor.executeRulesFor(event)
        then:
        noExceptionThrown()
    }

    // TODO this specification is rather dummy
    def "Should reset facts on not supported events"() {
        given:
        def weather = new Weather()
        def event = new WeatherStaleEvent(weather)
        when:
        executor.executeRulesFor(event)
        then:
        noExceptionThrown()
    }
}
