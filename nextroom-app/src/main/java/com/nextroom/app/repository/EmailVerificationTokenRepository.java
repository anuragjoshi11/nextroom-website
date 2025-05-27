package com.nextroom.app.repository;

import com.nextroom.app.model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.nextroom.app.model.User;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByUser(User user);
    Optional<EmailVerificationToken> findByToken(String token);
}

