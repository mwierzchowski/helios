package com.github.mwierzchowski.helios.core.rules;

import com.github.mwierzchowski.helios.core.commons.HeliosEvent;
import com.github.mwierzchowski.helios.core.scenes.SceneMaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RuleBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static java.util.stream.Collectors.toSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class SceneRuleExecutor {
    private final SceneRuleProperties properties;
    private final SceneRuleRepository repository;
    private final SceneMaker sceneMaker;
    private RulesEngine rulesEngine = new DefaultRulesEngine();
    private Facts facts = new Facts();

    // todo setup method for engine configuration
    // todo add listener that logs which rule is executed
    // todo delay after startup to collect as many facts as possible and then trigger
    @EventListener
    @Transactional
    public synchronized void executeRulesFor(HeliosEvent<?> event) {
        if (!updateFactsOn(event)) {
            log.debug("Event not configured for rules execution: {}", event);
            return;
        }
        log.debug("Executing rules for event: {}", event);
        rulesEngine.fire(activeRules(), facts);
    }

    private boolean updateFactsOn(HeliosEvent<?> event) {
        var eventClass = event.getClass();
        var fact = event.getSubject();
        var factName = fact.getClass().getSimpleName();
        if (properties.getFactEvents().contains(eventClass)) {
            facts.put(factName, fact);
            log.debug("Added to facts: {}", fact);
        } else if (properties.getResetEvents().contains(eventClass)) {
            facts.remove(factName);
            log.debug("Removed from facts: {}", fact);
        } else {
            return false;
        }
        return true;
    }

    private Rules activeRules() {
        var ruleSet = repository.getActiveRules().stream()
                .map(sceneRule -> new RuleBuilder()
                        .name(sceneRule.getClass().getSimpleName())
                        .priority(sceneRule.getPriority())
                        .when(facts -> sceneRule.check(facts.asMap()))
                        .then(facts -> sceneMaker.setup(sceneRule.getScene()))
                        .build())
                .collect(toSet());
        return new Rules(ruleSet);
    }
}
