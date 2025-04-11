package com.nichi.nikkie.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class MailConfiguration {

    private final XMLMapperConfiguration mailConfiguration;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(mailConfiguration.getMailHost());
        sender.setPort(mailConfiguration.getMailPort());
        sender.setUsername(mailConfiguration.getMailUsername());
        sender.setPassword(mailConfiguration.getMailPassword());

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", mailConfiguration.getMailProtocol());
        props.put("mail.smtp.auth", mailConfiguration.isMailAuth());
        props.put("mail.smtp.starttls.enable", mailConfiguration.isMailEnable());
        props.put("mail.debug", "true");

        return sender;
    }
}
