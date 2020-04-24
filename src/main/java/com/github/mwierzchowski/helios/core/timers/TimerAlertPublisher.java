package com.github.mwierzchowski.helios.core.timers;

import com.github.mwierzchowski.helios.core.commons.EventStore;
import com.github.mwierzchowski.helios.core.commons.HeliosEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Component responsible for publishing timer alerts.
 * @author Marcin Wierzchowski
 */
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class TimerAlertPublisher {
    /**
     * Timer repository
     */
    private final TimerRepository timerRepository;

    /**
     * Tasks scheduler
     */
    private final TaskScheduler taskScheduler;

    /**
     * Events store
     */
    private final EventStore eventStore;

    /**
     * Callback method called on application start. It starts alert tasks for all timers.
     */
    @EventListener(classes = ApplicationReadyEvent.class, condition = "@commonProperties.publishEventsOnStartup")
    public void startAlerts() {
        log.debug("Starting all alert tasks");
        timerRepository.findAll().forEach(this::startAlertFor);
    }

    /**
     * Start alert task for all schedules of given timer.
     * @param timer timer
     */
    public void startAlertFor(Timer timer) {
        log.info("Starting '{}' timer task", timer.getDescription());
        timer.getSchedules().forEach(this::scheduleTask);
    }

    /**
     * Helper method that creates schedule tasks/trigger for schedule and registers them in the scheduler.
     * @param schedule schedule of the timer
     */
    private void scheduleTask(TimerSchedule schedule) {
        TaskTrigger trigger = new TaskTrigger(schedule);
        Task task = new Task(schedule.getTimer(), schedule, trigger);
        taskScheduler.schedule(task, trigger);
    }

    /**
     * Class of alert task triggers.
     */
    @RequiredArgsConstructor
    static class TaskTrigger implements Trigger {
        /**
         * Schedule
         */
        private final TimerSchedule schedule;

        /**
         * Flag informing if trigger is stopped
         */
        private boolean stopped = false;

        /**
         * Main trigger method.
         * @param triggerContext trigger context
         * @return date of next execution or null when trigger is stopped
         */
        @Override
        public synchronized Date nextExecutionTime(TriggerContext triggerContext) {
            return stopped ? null : Date.from(schedule.nearestOccurrence());
        }

        /**
         * Stops trigger (next call to {@link TaskTrigger#nextExecutionTime(TriggerContext)} will give null).
         */
        public synchronized void stop() {
            this.stopped = true;
        }
    }

    /**
     * Class of alert tasks.
     */
    @RequiredArgsConstructor
    class Task implements Runnable {
        /**
         * Timer
         */
        private final Timer timer;

        /**
         * Schedule
         */
        private final TimerSchedule schedule;

        /**
         * Trigger
         */
        private final TaskTrigger trigger;

        /**
         * Main task method. On the beginning, method checks if given schedule is still valid. If not, trigger is
         * being stopped.
         */
        @Override
        public void run() {
            if (!isValid()) {
                log.debug("Schedule {} for timer {} is invalid", schedule.getId(), timer.getId());
                trigger.stop();
                return;
            }
            log.info("Publishing alert for timer '{}' (id: {})", timer.getDescription(), timer.getId());
            HeliosEvent event = new TimerAlertEvent(timer);
            eventStore.publish(event);
        }

        /**
         * Helper method that checks if task is still valid.
         * @return result of the check
         */
        private boolean isValid() {
            return timerRepository.findById(timer.getId())
                    .flatMap(foundTimer -> foundTimer.getSchedule(schedule.getId()))
                    .map(TimerSchedule::getVersion)
                    .map(foundVersion -> foundVersion.equals(schedule.getVersion()))
                    .orElse(false);
        }
    }
}
