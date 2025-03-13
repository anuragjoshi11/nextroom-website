package com.nextroom.app.controller;

import com.nextroom.app.dto.LoginResponse;
import com.nextroom.app.dto.UserLoginDTO;
import com.nextroom.app.dto.UserRegisterDTO;
import com.nextroom.app.model.User;
import com.nextroom.app.security.AuthenticationService;
import com.nextroom.app.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.nextroom.app.constants.Constants.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = FRONTEND_ORIGIN)
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService, JwtService jwtService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
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
    public ResponseEntity<String> registerStudent(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        userRegisterDTO.setRole(ROLE_STUDENT);
        User registeredUser = authenticationService.signup(userRegisterDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(STUDENT_USER_CREATED_SUCCESS);
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
        User authenticatedUser = authenticationService.authenticate(userLoginDTO, ROLE_STUDENT);
        String jwtToken = jwtService.generateToken(authenticatedUser);
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
        User authenticatedUser = authenticationService.authenticate(userLoginDTO, ROLE_LANDLORD);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
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
