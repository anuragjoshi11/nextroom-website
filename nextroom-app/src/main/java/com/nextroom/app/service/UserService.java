package com.nextroom.app.service;

import com.nextroom.app.dto.UserRegisterDTO;
import com.nextroom.app.model.User;

import java.util.List;

public interface UserService {

    public List<User> allUsers();
}
