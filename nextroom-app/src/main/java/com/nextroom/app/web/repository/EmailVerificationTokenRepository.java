package com.nextroom.app.web.repository;

import com.nextroom.app.web.model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.nextroom.app.web.model.User;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByUser(User user);
    Optional<EmailVerificationToken> findByToken(String token);
}

