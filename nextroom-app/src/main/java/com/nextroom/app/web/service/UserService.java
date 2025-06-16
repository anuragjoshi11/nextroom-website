package com.nextroom.app.web.service;

import com.nextroom.app.web.dto.UserRegisterDTO;
import com.nextroom.app.web.model.User;

import java.util.List;

public interface UserService {

    public List<User> allUsers();

    public User findUserByEmail(String email);

    public void registerStudent(UserRegisterDTO userRegisterDTO);

    public void sendEmailVerificationToken(User user);

    public void verifyEmail(String token);

    public void resendVerificationEmail(String email);
}
