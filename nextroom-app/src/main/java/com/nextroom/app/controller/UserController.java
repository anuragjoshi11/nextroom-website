package com.nextroom.app.controller;

import com.nextroom.app.dto.UserRequestDTO;
import com.nextroom.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.nextroom.app.constants.Constants.USER_CREATED_SUCCESS;

@RestController
@RequestMapping("/users")
//@CrossOrigin(origins = FRONTEND_ORIGIN)
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Registers a new user
     *
     * @param userRequestDTO the user data to be saved
     */
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        userService.saveUser(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(USER_CREATED_SUCCESS);
    }
}
