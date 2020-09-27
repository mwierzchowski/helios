package com.github.mwierzchowski.helios.adapter.mail

import com.github.mwierzchowski.helios.core.commons.CommonProperties
import com.github.mwierzchowski.helios.core.commons.FailureEvent
import org.springframework.mail.javamail.JavaMailSender
import spock.lang.Specification
import spock.lang.Subject

import javax.mail.internet.MimeMessage

class EventMailSenderSpec extends Specification {
    def commonProperties = new CommonProperties()
    def mailProperties = new MailProperties()
    def mailSender = Mock(JavaMailSender)
    def mimeMessage = Mock(MimeMessage)

    @Subject
    def eventMailSender = new EventMailSender(commonProperties, mailProperties, mailSender).tap {
        userName = "username"
        applicationName = "app"
    }

    def "Should send email if events were enqueued"() {
        given:
        def event1 = new FailureEvent("EventMailSenderSpec", new RuntimeException())
        def event2 = new FailureEvent("EventMailSenderSpec", new RuntimeException())
        mailSender.createMimeMessage() >> mimeMessage
        when:
        eventMailSender.enqueue(event1)
        eventMailSender.enqueue(event2)
        eventMailSender.sendMail()
        then:
        1 * mailSender.send({
            verifyAll(it, MimeMessage) {
                it == mimeMessage
            }
        })
    }

    def "Should not send email if events were not enqueued"() {
        when:
        eventMailSender.sendMail()
        then:
        0 * mailSender.send(_ as MimeMessage)
    }

    def "Should not send email if mail was already sent"() {
        given:
        mailSender.createMimeMessage() >> mimeMessage
        eventMailSender.enqueue(new FailureEvent("EventMailSenderSpec", new RuntimeException()))
        eventMailSender.sendMail()
        when:
        eventMailSender.sendMail()
        then:
        0 * mailSender.send(_ as MimeMessage)
    }

    def "Should send email if previous mail sending failed"() {
        given:
        mailSender.createMimeMessage() >>> [null, mimeMessage]
        eventMailSender.enqueue(new FailureEvent("EventMailSenderSpec", new RuntimeException()))
        eventMailSender.sendMail()
        when:
        eventMailSender.sendMail()
        then:
        1 * mailSender.send(_ as MimeMessage)
    }
}
