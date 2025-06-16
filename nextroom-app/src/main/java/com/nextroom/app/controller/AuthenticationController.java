package com.nextroom.app.controller;

import com.nextroom.app.dto.*;
import com.nextroom.app.model.User;
import com.nextroom.app.repository.PasswordResetTokenRepository;
import com.nextroom.app.security.AuthenticationService;
import com.nextroom.app.security.JwtService;
import com.nextroom.app.service.EmailService;
import com.nextroom.app.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.nextroom.app.constants.Constants.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5000", "https://nextroom-frontend.uc.r.appspot.com", "https://www.nextroom.ca", "https://nextroom.ca", "https://dev-nextroom-frontend.ue.r.appspot.com"})
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final EmailService emailService;
    private final PasswordResetService passwordResetService;


    @Autowired
    public AuthenticationController(AuthenticationService authenticationService, JwtService jwtService, EmailService emailService, PasswordResetService passwordResetService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.emailService = emailService;
        this.passwordResetService = passwordResetService;
    }

    /**
     * Registers a new user
     *
     * @param userRegisterDTO the user data to be saved
     */
    @Operation(
            summary = "Register a new student",
            description = "Registers a new student by accepting their details and creating a user account.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Student registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid user input")
            }
    )
    @PostMapping("/student/signup")
    public ResponseEntity<LoginResponse> registerStudent(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        userRegisterDTO.setRole(ROLE_STUDENT);
        User registeredUser = authenticationService.signup(userRegisterDTO);

        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail(userRegisterDTO.getEmail());
        userLoginDTO.setPassword(userRegisterDTO.getPassword());

        User authenticatedUser = authenticationService.authenticate(userLoginDTO);

        String jwtToken = jwtService.generateToken(authenticatedUser, ROLE_STUDENT);
        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());

        emailService.sendSignupEmail(registeredUser.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
    }

    @Operation(
            summary = "Register a new landlord",
            description = "Registers a new landlord by accepting their details and creating a user account.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Landlord registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid user input")
            }
    )
    @PostMapping("/landlord/signup")
    public ResponseEntity<String> registerLandlord(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        userRegisterDTO.setRole(ROLE_LANDLORD);
        User registeredUser = authenticationService.signup(userRegisterDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(LANDLORD_USER_CREATED_SUCCESS);
    }

    @Operation(
            summary = "Student Login",
            description = "Authenticates the student and generates a JWT token for further access.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials")
            }
    )
    @PostMapping("/student/login")
    public ResponseEntity<LoginResponse> studentLogin(@RequestBody UserLoginDTO userLoginDTO) {
        User authenticatedUser = authenticationService.authenticate(userLoginDTO);
        String jwtToken = jwtService.generateToken(authenticatedUser, ROLE_STUDENT);
        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(
            summary = "Landlord Login",
            description = "Authenticates the landlord and generates a JWT token for further access.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials")
            }
    )
    @PostMapping("/landlord/login")
    public ResponseEntity<LoginResponse> landlordLogin(@RequestBody UserLoginDTO userLoginDTO) {
        User authenticatedUser = authenticationService.authenticate(userLoginDTO);
        String jwtToken = jwtService.generateToken(authenticatedUser, ROLE_LANDLORD);
        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPasswordRequest(@RequestBody @Valid ForgotPasswordRequestDTO request) {
        passwordResetService.processPasswordResetRequest(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "Password reset link has been sent to your email."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequestDTO request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Password has been successfully reset."));
    }

        //Below code for subdomain:

    /*@PostMapping
    public ResponseEntity<String> registerUser(
            @Valid @RequestBody UserRegisterDTO userRegisterDTO,
            HttpServletRequest request) {

        // Extract subdomain
        String subdomain = getSubdomain(request);

        // Assign role based on subdomain
        if ("students".equals(subdomain)) {
            userRegisterDTO.setRole("STUDENT");
        } else if ("partners".equals(subdomain)) {
            userRegisterDTO.setRole("LANDLORD");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid subdomain");
        }

        // Save the user
        userService.saveUser(userRegisterDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully.");
    }

    private String getSubdomain(HttpServletRequest request) {
        String host = request.getServerName(); // Get the domain name (e.g., students.nextroom.ca)
        String[] parts = host.split("\\."); // Split by dot
        if (parts.length > 2) {
            return parts[0]; // First part is the subdomain (students or partners)
        }
        return ""; // If no subdomain, return empty
    }*/
}