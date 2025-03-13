package com.nextroom.app.controller;

import com.nextroom.app.model.User;
import com.nextroom.app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.nextroom.app.constants.Constants.FRONTEND_ORIGIN;

@RequestMapping("/users")
@RestController
@CrossOrigin(origins = FRONTEND_ORIGIN)
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get all users",
            description = "Fetches a list of all users in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of all users", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "404", description = "No users found")
            }
    )
    @GetMapping
    public ResponseEntity<List<User>> allUsers() {
        List <User> users = userService.allUsers();
        return ResponseEntity.ok(users);
    }

    /*@GetMapping("/{id}")
    public ResponseEntity<List<User>> findUserById() {
        List <User> users = userService.allUsers();

        return ResponseEntity.ok(users);
    }*/

    @Operation(
            summary = "Get current authenticated user",
            description = "Fetches the details of the currently authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authenticated user details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
                    @ApiResponse(responseCode = "401", description = "User not authenticated")
            }
    )
    @GetMapping("/current")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(currentUser);
    }
}
