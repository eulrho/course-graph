package com.course_graph.repository;

import com.course_graph.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    Optional<TokenEntity> findByToken(String token);
    Optional<TokenEntity> findByEmail(String email);
    void deleteByExpiredAt(Date time);
    void deleteByToken(String token);
    void deleteByEmail(String email);
}
