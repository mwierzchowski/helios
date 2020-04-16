package com.github.mwierzchowski.helios.core.timers;

import com.github.mwierzchowski.helios.core.HeliosEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class TimerAlertLauncher {
    private final TimerRepository timerRepository;
    private final TaskScheduler taskScheduler;
    private final ApplicationEventPublisher eventPublisher;

    // TODO start with application
    public void launchAlerts() {
        log.debug("Launching all alert tasks");
        timerRepository.findAll().forEach(this::launchAlertFor);
    }

    public void launchAlertFor(Timer timer) {
        log.info("Launching '{}' timer task", timer.getDescription());
        timer.getSchedules().forEach(this::startTask);
    }

    private void startTask(TimerSchedule schedule) {
        TaskTrigger trigger = new TaskTrigger(schedule);
        Task task = new Task(schedule.getTimer(), schedule, trigger);
        taskScheduler.schedule(task, trigger);
    }

    @RequiredArgsConstructor
    static class TaskTrigger implements Trigger {
        private final TimerSchedule schedule;
        private boolean stopped = false;

        @Override
        public synchronized Date nextExecutionTime(TriggerContext triggerContext) {
            return stopped ? null : Date.from(schedule.nearestOccurrence());
        }

        public synchronized void stop() {
            this.stopped = true;
        }
    }

    @RequiredArgsConstructor
    class Task implements Runnable {
        private final Timer timer;
        private final TimerSchedule schedule;
        private final TaskTrigger trigger;

        @Override
        public void run() {
            if (!isValid()) {
                log.debug("Schedule {} for timer {} is invalid", schedule.getId(), timer.getId());
                trigger.stop();
                return;
            }
            log.info("Publishing alert for timer '{}' (id: {})", timer.getDescription(), timer.getId());
            HeliosEvent event = new TimerAlertEvent(timer);
            eventPublisher.publishEvent(event);
        }

        private boolean isValid() {
            return timerRepository.findById(timer.getId())
                    .flatMap(foundTimer -> foundTimer.getSchedule(schedule.getId()))
                    .map(TimerSchedule::getVersion)
                    .map(foundVersion -> foundVersion.equals(schedule.getVersion()))
                    .orElse(false);
        }
    }
}
