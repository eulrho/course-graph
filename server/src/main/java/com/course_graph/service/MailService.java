package com.course_graph.service;

import com.course_graph.mail.MailConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final MailConfig mailConfig;

    public MimeMessage createMail(String email, String key) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        helper.setTo(email);
        helper.setSubject("이메일 인증");
        helper.setFrom(mailConfig.getFromMail());

        String body = "";
        body += "<h3>요청하신 인증 번호입니다.</h3>";
        body += "<h1>" + key + "</h1>";
        body += "<h3>감사합니다.</h3>";
        helper.setText(body, true);
        return message;
    }

    public void sendCodeMail(MimeMessage message) {
        javaMailSender.send(message);
    }
}
