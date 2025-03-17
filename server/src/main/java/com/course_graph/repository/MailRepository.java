package com.course_graph.repository;

import com.course_graph.entity.VerificationCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MailRepository extends JpaRepository<VerificationCodeEntity, Long> {
    Optional<VerificationCodeEntity> findByEmail(String email);
    void deleteByExpiredAt(LocalDateTime time);
    void deleteByEmail(String email);
}
