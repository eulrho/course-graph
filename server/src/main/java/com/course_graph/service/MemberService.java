package com.course_graph.service;

import com.course_graph.Exception.RestApiException;
import com.course_graph.dto.MemberMailVerifyRequest;
import com.course_graph.entity.VerificationCodeEntity;
import com.course_graph.enums.CustomErrorCode;
import com.course_graph.repository.MailRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MailService mailService;
    private final MailRepository mailRepository;

    public String createNumber() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(3);
            switch (index) {
                case 0:
                    key.append((char) ((random.nextInt(26)) + 97)); // lower
                    break;
                case 1:
                    key.append((char) ((random.nextInt(26)) + 65)); // upper
                    break;
                case 2:
                    key.append(random.nextInt(10)); // number
                    break;
            }
        }
        return key.toString();
    }

    @Transactional
    public VerificationCodeEntity createVerificationCode(String email) {
        String newCode = createNumber();

        if (mailRepository.findByEmail(email).isPresent())
            mailRepository.deleteByEmail(email);
        VerificationCodeEntity verificationCodeEntity = VerificationCodeEntity.toVerificationCodeEntity(email, newCode, LocalDateTime.now().plusDays(1));
        return mailRepository.save(verificationCodeEntity);
    }

    @Transactional
    public void sendMail(String email) {
        try {
            VerificationCodeEntity verificationCodeEntity = createVerificationCode(email);
            MimeMessage message = mailService.createMail(email, verificationCodeEntity.getCode());
            mailService.sendCodeMail(message);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            throw new RestApiException(CustomErrorCode.FAILED_TO_SEND_MAIL);
        }
    }

    @Transactional
    public void verifyCode(MemberMailVerifyRequest memberMailVerifyRequest) {
        VerificationCodeEntity verificationCodeEntity = mailRepository.findByEmail(memberMailVerifyRequest.getEmail())
                .orElseThrow(() -> new RestApiException(CustomErrorCode.NO_MATCH_EMAIL));
        if (!verificationCodeEntity.getCode().equals(memberMailVerifyRequest.getCode()))
            throw new RestApiException(CustomErrorCode.NO_MATCH_CODE);
        if (verificationCodeEntity.getExpiredAt().isBefore(LocalDateTime.now()))
            throw new RestApiException(CustomErrorCode.EXPIRED_CODE);
        verificationCodeEntity.verify();
    }

    public void checkVerification(String email) {
        VerificationCodeEntity verificationCodeEntity = mailRepository.findByEmail(email)
                .orElseThrow(() -> new RestApiException(CustomErrorCode.NOT_VERIFIED));
        if (!verificationCodeEntity.isVerified())
            throw new RestApiException(CustomErrorCode.NOT_VERIFIED);
    }

    @Transactional
    @Scheduled(cron = "0 0 12 * * ?")
    public void deleteExpiredVerificationCodeEntity() {
        mailRepository.deleteByExpiredAt(LocalDateTime.now());
    }
}
