package com.github.mwierzchowski.helios.core.commons;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Clock;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Configuration for common components.
 * @author Marcin Wierzchowski
 */
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

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(5);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        var taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);
        taskScheduler.setThreadNamePrefix("helios-scheduler");
        return taskScheduler;
    }
}
