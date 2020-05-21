package com.github.mwierzchowski.helios.core.rules;

import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RulesConfiguration {
    @Bean
    public RulesEngine rulesEngine() {
        // TODO parameters
        return new DefaultRulesEngine();
    }

    @Bean
    public Rules rules() {
        // TODO registration
        return new Rules();
    }
}
