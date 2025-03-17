package com.course_graph.mail;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Data
@Configuration
@EnableConfigurationProperties
public class MailConfig {
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.port}")
    private int port;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.from-mail}")
    private String fromMail;

    @Value("${spring.mail.properties.mail.debug}")
    private boolean debug;
    @Value("${spring.mail.properties.mail.smtp.connectiontimeout}")
    private int connectionTimeout;
    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private boolean starttlsEnable;
    @Value("${spring.mail.properties.mail.smtp.starttls.required}")
    private boolean starttlsRequired;
    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean auth;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(this.host);
        javaMailSender.setPort(this.port);
        javaMailSender.setUsername(this.username);
        javaMailSender.setPassword(this.password);
        javaMailSender.setJavaMailProperties(getMailProperties());
        return javaMailSender;
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", this.auth);
        properties.put("mail.smtp.starttls.enable", this.starttlsEnable);
        properties.put("mail.smtp.starttls.required", this.starttlsRequired);
        properties.put("mail.smtp.connectiontimeout", this.connectionTimeout);
        properties.put("mail.debug", this.debug);
        return properties;
    }
}
