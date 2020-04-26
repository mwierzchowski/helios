package com.github.mwierzchowski.helios.adapter.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties for mail adapter
 * @author Marcin Wierzchowski
 */
@Data
@Component
@ConfigurationProperties("helios.mail")
public class MailProperties {
    /**
     * Recipient email address (you)
     */
    private String recipient = "REPLACE_WITH_YOUR_RECIPIENT";

    /**
     * Email service url
     */
    private String service = "gmail.com";

    /**
     * Cron describing when mails should be sent
     */
    private String sendCron = "0 0 6-22 * * *";
}
