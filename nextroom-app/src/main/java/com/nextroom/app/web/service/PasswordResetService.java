package com.nextroom.app.web.service;

import com.nextroom.app.web.dto.ResetPasswordRequestDTO;

public interface PasswordResetService {
    public void resetPassword(ResetPasswordRequestDTO request);
    public void processPasswordResetRequest(String email);
}
