package com.nextroom.app.web.service.impl;

import com.nextroom.app.common.exception.EntityNotFoundException;
import com.nextroom.app.web.dto.ResetPasswordRequestDTO;
import com.nextroom.app.common.exception.InvalidTokenException;
import com.nextroom.app.common.exception.TokenExpiredException;
import com.nextroom.app.common.exception.UserNotLinkedToTokenException;
import com.nextroom.app.web.model.PasswordResetToken;
import com.nextroom.app.web.model.User;
import com.nextroom.app.web.repository.PasswordResetTokenRepository;
import com.nextroom.app.web.repository.UserRepository;
import com.nextroom.app.web.service.EmailService;
import com.nextroom.app.web.service.PasswordResetService;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final Logger logger = LogManager.getLogger(PasswordResetServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordResetServiceImpl(UserRepository userRepository,
                                    PasswordResetTokenRepository passwordResetTokenRepository,
                                    EmailService emailService,
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void processPasswordResetRequest(String email) {
        logger.info("Processing password reset request for email: {}", email);

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("No user registered with that email."));

            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken(token, user);

            passwordResetTokenRepository.save(resetToken);
            logger.info("Password reset token saved for user email={}", user.getEmail());

            String resetLink = "https://nextroom.ca/reset-password?token=" + token;
            emailService.sendPasswordResetEmail(email, resetLink);
            logger.info("Reset email sent to {}", email);

        } catch (EntityNotFoundException e) {
            logger.warn("Validation error: {}", e.getMessage());
            throw e;

        } catch (DataAccessException | PersistenceException e) {
            logger.error("Database error during password reset request", e);
            throw new RuntimeException("Internal server error while processing password reset request.");

        } catch (Exception e) {
            logger.error("Unexpected error during password reset request", e);
            throw new RuntimeException("An unexpected error occurred. Please try again later.");
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequestDTO request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();

        try {
            logger.info("Resetting password using token");

            PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                    .orElseThrow(() -> new InvalidTokenException("Invalid password reset token."));

            if (resetToken.isExpired()) {
                logger.warn("Token expired for user: {}", resetToken.getUser().getEmail());
                throw new TokenExpiredException("Password reset token has expired.");
            }

            User user = resetToken.getUser();
            if (user == null) {
                logger.error("User not found for token: {}", token);
                throw new UserNotLinkedToTokenException("User not found for the provided token.");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            logger.info("Password updated for user: {}", user.getEmail());

            passwordResetTokenRepository.delete(resetToken);
            logger.info("Password reset token invalidated for user: {}", user.getEmail());

        } catch (InvalidTokenException | TokenExpiredException | UserNotLinkedToTokenException e) {
            logger.warn("Validation error: {}", e.getMessage());
            throw e;

        } catch (DataAccessException | PersistenceException e) {
            logger.error("Database error during password reset", e);
            throw new RuntimeException("Internal server error while resetting password.");

        } catch (Exception e) {
            logger.error("Unexpected error during password reset", e);
            throw new RuntimeException("An unexpected error occurred. Please try again later.");
        }
    }
}