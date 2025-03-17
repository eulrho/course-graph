package com.course_graph.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Table(name = "token")
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String email;

    @Column(nullable = false, length = 500)
    private String token;

    @Column(nullable = false)
    private Date expiredAt;

    @Builder
    public static TokenEntity toTokenEntity(String token, String email, Date expiredAt) {
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.token = token;
        tokenEntity.email = email;
        tokenEntity.expiredAt = expiredAt;
        return tokenEntity;
    }
}
