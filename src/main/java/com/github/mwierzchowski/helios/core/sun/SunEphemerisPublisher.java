package com.github.mwierzchowski.helios.core.sun;

import com.github.mwierzchowski.helios.core.commons.EventStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Publisher of ephemeris events. Each event is published at the time when given type of event happens.
 * @author Marcin Wierzchowski
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SunEphemerisPublisher {
    /**
     * Clock
     */
    private final Clock clock;

    /**
     * Ephemeris provider
     */
    private final SunEphemerisProvider ephemerisProvider;

    /**
     * Executor service
     */
    private final ScheduledExecutorService executorService;

    /**
     * Event store
     */
    private final EventStore eventStore;

    /**
     * Next event to happen
     */
    private SunEphemerisEvent event;

    /**
     * Starts publishing events. Method could be automatically called on application startup.
     */
    @EventListener(classes = ApplicationReadyEvent.class, condition = "@commonProperties.processingOnStartupEnabled")
    public void startPublishingEvents() {
        log.info("Starting sun events");
        scheduleNextEventPublish();
    }

    /**
     * Helper method that configures schedule of next event.
     */
    private void scheduleNextEventPublish() {
        var today = LocalDate.now(clock);
        event = ephemerisProvider.sunEphemerisFor(today).firstEventAfterPreviousOrNow(event, clock)
                .orElseGet(() -> ephemerisProvider.sunEphemerisFor(today.plusDays(1)).firstEventOfDay(clock));
        var delay = event.getDelay(clock);
        log.debug("Next event will be {} in {}h {}min {}s", event.getSubject(),
                delay.toHoursPart(), delay.toMinutesPart(), delay.toSecondsPart());
        executorService.schedule(this::publishEvent, delay.toMillis(), MILLISECONDS);
    }

    /**
     * Publish event.
     */
    private synchronized void publishEvent() {
        log.info("Publishing {} event", event.getSubject());
        eventStore.publish(event);
        scheduleNextEventPublish();
    }
}
