package com.nextroom.app.service;

import com.nextroom.app.dto.ResetPasswordRequestDTO;

public interface PasswordResetService {
    public void resetPassword(ResetPasswordRequestDTO request);
    public void processPasswordResetRequest(String email);
}
