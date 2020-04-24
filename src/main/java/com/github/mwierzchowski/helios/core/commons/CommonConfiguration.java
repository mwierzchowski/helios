package com.github.mwierzchowski.helios.core.commons;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class CommonConfiguration {
    @Bean
    public EventStore eventStore(ApplicationEventPublisher publisher) {
        return publisher::publishEvent;
    }

    @Bean
    public Clock systemClock() {
        return  Clock.systemDefaultZone();
    }
}
