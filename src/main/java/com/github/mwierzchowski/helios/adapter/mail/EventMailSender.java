package com.github.mwierzchowski.helios.adapter.mail;

import com.github.mwierzchowski.helios.core.commons.CommonProperties;
import com.github.mwierzchowski.helios.core.commons.FailureEvent;
import com.github.mwierzchowski.helios.core.commons.TimestampedHeliosEvent;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.joining;

/**
 * Component responsible for sending emails based on received events.
 * @author Marcin Wierzchowski
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.mail.host")
public class EventMailSender {
    /**
     * Common properties
     */
    private final CommonProperties commonProperties;

    /**
     * Mail properties
     */
    private final MailProperties mailProperties;

    /**
     * Mail sender
     */
    private final JavaMailSender mailSender;

    /**
     * Username sending emails
     */
    @Setter
    @Value("${spring.mail.username}")
    private String userName;

    /**
     * Application name sending emails
     */
    @Setter
    @Value("${info.app.name}")
    private String applicationName;

    /**
     * Queue of events for send
     */
    private Queue<TimestampedHeliosEvent> eventQueue = new ConcurrentLinkedQueue<>();

    /**
     * Callback post construct method that reports mail notifications availability.
     */
    @PostConstruct
    public void reportStatus() {
        log.info("Mail notifications are enabled");
    }

    /**
     * Add to queue failure event
     * @param failureEvent event
     */
    @EventListener
    public synchronized void enqueue(FailureEvent failureEvent) {
        log.debug("Enqueuing failure event: {}", failureEvent);
        eventQueue.add(failureEvent);
    }

    /**
     * Sends email with events since last send. Events are removed from queue but they are put back when send fails.
     */
    @Scheduled(cron = "#{mailProperties.sendCron}")
    public void sendMail() {
        if (eventQueue.isEmpty()) {
            log.debug("Events queue is empty, nothing to send");
            return;
        }
        Queue<TimestampedHeliosEvent> sendQueue;
        synchronized (this) {
            sendQueue = new LinkedList<>(eventQueue);
            eventQueue.clear();
        }
        try {
            var subject = buildSubject(sendQueue);
            var text = buildText(sendQueue);
            var message = buildMessage(subject, text);
            mailSender.send(message);
            log.info("Mail '{}' was sent", subject);
        } catch (Exception ex) {
            eventQueue.addAll(sendQueue);
            log.error("Mail sending failed", ex);
        }
    }

    /**
     * Helper method that builds subject of the email.
     * @param sendQueue queue of events to be send
     * @return subject
     */
    private String buildSubject(Queue<TimestampedHeliosEvent> sendQueue) {
        var failureCount = sendQueue.stream().filter(event -> event instanceof FailureEvent).count();
        var subjectTemplate = failureCount == 1 ? "{0} status: {1} alert" : "{0} status: {1} alerts";
        return format(subjectTemplate, applicationName, failureCount);
    }

    /**
     * Helper method that builds text of email.
     * @param sendQueue queue of events to be send
     * @return text
     */
    private String buildText(Queue<TimestampedHeliosEvent> sendQueue) {
        Function<TimestampedHeliosEvent, String> eventToText = (e) -> {
            var event = (FailureEvent) e;
            var time = event.getZonedDateTime().format(commonProperties.timeFormatter());
            var clazz = event.getSource().getSimpleName();
            var message = event.getThrowable().getMessage();
            return format("{0} - failure in {1}: {2}", time, clazz, message);
        };
        return sendQueue.stream()
                .sorted()
                .map(eventToText)
                .collect(joining("<br>"));
    }

    /**
     * Helper method that builds mail message
     * @param subject subject of email
     * @param text text of email
     * @return message
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     */
    private MimeMessage buildMessage(String subject, String text) throws UnsupportedEncodingException,
            MessagingException {
        var sender = format("{0}@{1}", userName, mailProperties.getService());
        var message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message, false);
        messageHelper.setFrom(sender, applicationName);
        messageHelper.setTo(mailProperties.getRecipient());
        messageHelper.setSubject(subject);
        messageHelper.setText(text, true);
        return message;
    }
}
