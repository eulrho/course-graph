package com.course_graph.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "mail")
public class VerificationCodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 100)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    private boolean isVerified;

    @Builder
    public static VerificationCodeEntity toVerificationCodeEntity(String email, String code, LocalDateTime expiredAt) {
        VerificationCodeEntity verificationCodeEntity = new VerificationCodeEntity();
        verificationCodeEntity.email = email;
        verificationCodeEntity.code = code;
        verificationCodeEntity.expiredAt = expiredAt;
        verificationCodeEntity.isVerified = false;
        return verificationCodeEntity;
    }

    public void verify() {
        this.isVerified = true;
    }
}
