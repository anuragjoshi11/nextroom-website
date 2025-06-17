package com.nextroom.app.web.service;

import com.nextroom.app.web.dto.InviteResponseDTO;
import com.nextroom.app.web.dto.InviteActionRequestDTO;
import com.nextroom.app.web.dto.InviteActionResponseDTO;
import com.nextroom.app.web.dto.InviteValidationResponseDTO;
import com.nextroom.app.web.model.User;

public interface InviteService {

    public InviteResponseDTO createInvite(User user);

    public InviteValidationResponseDTO validateInviteSlug(String slug);

    public InviteActionResponseDTO handleInviteAction(Long inviteId, InviteActionRequestDTO requestDTO, User invitee);
}
