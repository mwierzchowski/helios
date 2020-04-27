package com.github.mwierzchowski.helios.adapter.mail

import com.github.mwierzchowski.helios.core.commons.CommonProperties
import com.github.mwierzchowski.helios.core.commons.FailureEvent
import org.springframework.mail.javamail.JavaMailSender
import spock.lang.Specification
import spock.lang.Subject

import javax.mail.internet.MimeMessage

class MailListenerSpec extends Specification {
    def commonProperties = new CommonProperties()
    def mailProperties = new MailProperties()
    def mailSender = Mock(JavaMailSender)
    def mimeMessage = Mock(MimeMessage)

    @Subject
    def mailListener = new MailListener(commonProperties, mailProperties, mailSender).tap {
        userName = "username"
        applicationName = "app"
    }

    def "Should send email if events were published"() {
        given:
        def message1 = "failure 1"
        def event1 = new FailureEvent(MailListenerSpec, new RuntimeException(message1))
        def message2 = "failure 2"
        def event2 = new FailureEvent(MailListenerSpec, new RuntimeException(message2))
        mailSender.createMimeMessage() >> mimeMessage
        when:
        mailListener.onFailure(event1)
        mailListener.onFailure(event2)
        mailListener.sendMail()
        then:
        1 * mailSender.send({
            verifyAll(it, MimeMessage) {
                it == mimeMessage
            }
        })
        1 * mimeMessage.setSubject({it.contains('2')})
    }

    def "Should not send email if events were not published"() {
        when:
        mailListener.sendMail()
        then:
        0 * mailSender.send(_ as MimeMessage)
    }

    def "Should not send email if mail was already sent"() {
        given:
        mailSender.createMimeMessage() >> mimeMessage
        mailListener.onFailure(new FailureEvent(MailListenerSpec, new RuntimeException()))
        mailListener.sendMail()
        when:
        mailListener.sendMail()
        then:
        0 * mailSender.send(_ as MimeMessage)
    }

    def "Should send email if previous mail failed"() {
        given:
        mailSender.createMimeMessage() >>> [null, mimeMessage]
        mailListener.onFailure(new FailureEvent(MailListenerSpec, new RuntimeException()))
        try {
            mailListener.sendMail()
        } catch (Exception ex) {
            // ignore
        }
        when:
        mailListener.sendMail()
        then:
        1 * mailSender.send(_ as MimeMessage)
    }
}
