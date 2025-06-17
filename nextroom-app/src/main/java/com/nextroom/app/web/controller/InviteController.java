package com.nextroom.app.web.controller;

import com.nextroom.app.web.dto.InviteActionRequestDTO;
import com.nextroom.app.web.dto.InviteActionResponseDTO;
import com.nextroom.app.web.dto.InviteResponseDTO;
import com.nextroom.app.web.dto.InviteValidationResponseDTO;
import com.nextroom.app.web.model.User;
import com.nextroom.app.web.service.InviteService;
import com.nextroom.app.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invite")
public class InviteController {

    private final InviteService inviteService;
    private final UserService userService;

    @Autowired
    public InviteController(InviteService inviteService, UserService userService) {
        this.inviteService = inviteService;
        this.userService = userService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<InviteResponseDTO> createInvite(@RequestHeader("Authorization") String token) {
        User inviter = userService.extractUserFromToken(token);
        InviteResponseDTO response = inviteService.createInvite(inviter);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate/{slug}")
    public ResponseEntity<InviteValidationResponseDTO> validateInvite(@PathVariable String slug) {
        InviteValidationResponseDTO response = inviteService.validateInviteSlug(slug);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/action/{inviteId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<InviteActionResponseDTO> handleInviteAction(
            @PathVariable Long inviteId,
            @RequestBody InviteActionRequestDTO requestDTO,
            @RequestHeader("Authorization") String token) {

        User invitee = userService.extractUserFromToken(token);
        InviteActionResponseDTO response = inviteService.handleInviteAction(inviteId, requestDTO, invitee);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(response);
    }
}
