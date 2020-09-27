package com.github.mwierzchowski.helios.core.rules

import com.github.mwierzchowski.helios.core.timers.Timer
import com.github.mwierzchowski.helios.core.timers.TimerAlertEvent
import spock.lang.Specification

class SceneRuleExecutorSpec extends Specification {
    def properties = new SceneRuleProperties()
    def executor = new SceneRuleExecutor(properties)

    def "Dummy test to be replaced later"() {
        given:
        def timer = new Timer()
        def event = new TimerAlertEvent(timer)
        when:
        executor.executeRulesFor(event)
        then:
        noExceptionThrown()
    }
}
