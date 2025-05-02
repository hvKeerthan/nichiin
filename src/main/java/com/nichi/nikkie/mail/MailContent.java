package com.nichi.nikkie.mail;

import com.nichi.nikkie.configuration.MailConfiguration;
import com.nichi.nikkie.configuration.XMLMapperConfiguration;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailContent {

    private final JavaMailSender javaMailSender;
    private final XMLMapperConfiguration mailConfiguration;

    public void sendSuccessMail() {
        sendMail(mailConfiguration.getMailSuccessContent() , "Success Mail");
    }

    public void sendDownloadFailedMail() {
        sendMail(mailConfiguration.getMailErrorContent(), "Download Failed Mail");
    }

    public void sendDatabaseFailedMail() {
        sendMail(mailConfiguration.getMailDbErrorContent(), "Database Failed Mail");
    }

    public void sendScrapeSuccessMail(){ sendMail(mailConfiguration.getMailScrapeSuccessContent(), "Scrape Success Mail");}

    public void sendScrapeErrorMail(){ sendMail(mailConfiguration.getMailScrapeErrorContent() , "Scrape Error mail");}

    private void sendMail(String content, String logPrefix) {
        try {
            List<String> email = List.of(mailConfiguration.getMailCc().split(","));
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(mailConfiguration.getMailTo());
//            helper.setCc(email.toArray(new String[0]));
            helper.setSubject(mailConfiguration.getMailSub());
            helper.setText(content);

            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("{} - Error while sending mail: {}", logPrefix, e.getMessage());
        }
    }
}
