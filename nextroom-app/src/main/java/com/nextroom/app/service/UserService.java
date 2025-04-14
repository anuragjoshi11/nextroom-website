package com.nextroom.app.service;

import com.nextroom.app.model.User;

import java.util.List;

public interface UserService {

    public List<User> allUsers();

    public User findUserByEmail(String email);
}
