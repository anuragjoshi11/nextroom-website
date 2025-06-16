package com.nextroom.app.controller;

import com.nextroom.app.dto.EmailRequestDTO;
import com.nextroom.app.service.VanityEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/promotion")
@RestController
@CrossOrigin(origins = {"http://localhost:5000", "https://nextroom-frontend.uc.r.appspot.com",
        "https://www.nextroom.ca", "https://nextroom.ca", "https://dev-nextroom-frontend.ue.r.appspot.com"})

public class VanityEmailController {

    private final VanityEmailService vanityEmailService;

    @Autowired
    public VanityEmailController(VanityEmailService vanityEmailService) {
        this.vanityEmailService = vanityEmailService;
    }

    @PostMapping("/send-email")
    //@PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO emailRequestDTO, @RequestHeader("Authorization") String token) {
        String result = vanityEmailService.sendVanityEmail(emailRequestDTO, token);
        if (result.equals("Emails sent successfully.")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
