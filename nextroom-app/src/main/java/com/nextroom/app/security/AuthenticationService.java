package com.nextroom.app.security;

import com.nextroom.app.dto.UserLoginDTO;
import com.nextroom.app.dto.UserRegisterDTO;
import com.nextroom.app.exception.AccountNotActivatedException;
import com.nextroom.app.exception.UserAlreadyExistsException;
import com.nextroom.app.model.User;
import com.nextroom.app.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private static final Logger logger = LogManager.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(UserRegisterDTO input) {
        try {

            User user = new User()
                    .setFirstName(input.getFirstName())
                    .setLastName(input.getLastName())
                    .setEmail(input.getEmail())
                    .setStatus(false) // set false and then activate by verification email
                    .setRole(input.getRole())
                    .setUniversity(input.getUniversity())
                    .setAge(input.getAge())
                    .setPhoneNumber(input.getPhoneNumber())
                    .setPronouns(input.getPronouns())
                    .setTag(input.getTag())
                    .setPassword(passwordEncoder.encode(input.getPassword()));

            User savedUser = userRepository.save(user);

            logger.info("User registered successfully: {}", savedUser.getEmail());
            return savedUser;

        } catch (DataIntegrityViolationException e) {
            logger.error("User registration failed for {}: Email already exists or constraint violation", input.getEmail());
            throw new UserAlreadyExistsException("The email address entered is already registered or invalid. Please use a different email.");
        } catch (Exception e) {
            logger.error("Unexpected error during user registration for {}: {}", input.getEmail(), e.getMessage());
            throw new RuntimeException("Internal server error during registration");
        }
    }

    public User authenticate(UserLoginDTO input) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );

            logger.info("Authentication successful for user: {}", input.getEmail());

            User user = userRepository.findByEmail(input.getEmail())
                    .orElseThrow(() -> {
                        logger.error("User not found after authentication: {}", input.getEmail());
                        return new RuntimeException("User not found");
                    });

            if (Boolean.FALSE.equals(user.getStatus())) {
                logger.error("User {} attempted login but account is not activated", input.getEmail());
                throw new AccountNotActivatedException("Account not verified. Please check your email.");
            }

            return user;

        } catch (BadCredentialsException e) {
            logger.error("Authentication failed for user {}: Invalid credentials", input.getEmail());
            throw new DataIntegrityViolationException("Invalid email or password");
        } catch (AccountNotActivatedException e) {
            logger.error("Account not activated: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during authentication for user {}: {}", input.getEmail(), e.getMessage());
            throw new RuntimeException("Internal server error during authentication");
        }
    }
}
