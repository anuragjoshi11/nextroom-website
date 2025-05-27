package com.nextroom.app.service.impl;

import com.nextroom.app.dto.LoginResponse;
import com.nextroom.app.dto.UserLoginDTO;
import com.nextroom.app.dto.UserRegisterDTO;
import com.nextroom.app.model.EmailVerificationToken;
import com.nextroom.app.model.User;
import com.nextroom.app.repository.EmailVerificationTokenRepository;
import com.nextroom.app.repository.UserRepository;
import com.nextroom.app.security.AuthenticationService;
import com.nextroom.app.security.JwtService;
import com.nextroom.app.service.EmailService;
import com.nextroom.app.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.nextroom.app.constants.Constants.ROLE_STUDENT;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final EmailService emailService;
    private final EmailVerificationTokenRepository tokenRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtService jwtService, AuthenticationService authenticationService, EmailService emailService, EmailVerificationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public List<User> allUsers() {
        return new ArrayList<>(userRepository.findAll());
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User not found with email: %s", email)));
    }

    @Override
    public void registerStudent(UserRegisterDTO userRegisterDTO) {
        userRegisterDTO.setRole(ROLE_STUDENT);

        User registeredUser = authenticationService.signup(userRegisterDTO);
        sendEmailVerificationToken(registeredUser);

        logger.info("Registration successful for {}. Awaiting email verification.", registeredUser.getEmail());

        //emailService.sendSignupEmail(registeredUser.getEmail());
    }

    @Override
    public void sendEmailVerificationToken(User user) {
        // Remove old token if exists
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        // Create new token
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = EmailVerificationToken.createTokenForUser(user, token);
        tokenRepository.save(verificationToken);

        String verifyLink = "https://nextroom.ca/verify-email?token=" + token;

        // Send email
        emailService.sendVerificationEmail(user.getEmail(), verifyLink);
    }

    @Override
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token."));

        if (verificationToken.isExpired()) {
            tokenRepository.delete(verificationToken); // optional
            throw new IllegalStateException("Token has expired. Please request a new verification email.");
        }

        User user = verificationToken.getUser();
        if (Boolean.TRUE.equals(user.getStatus())) {
            tokenRepository.delete(verificationToken); // optional cleanup
            throw new IllegalStateException("Account already verified.");
        }

        user.setStatus(true);
        userRepository.save(user);
        tokenRepository.delete(verificationToken); // cleanup
    }

    @Override
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No account registered with this email."));

        if (Boolean.TRUE.equals(user.getStatus())) {
            throw new IllegalStateException("Account is already verified.");
        }

        sendEmailVerificationToken(user);
    }


}
