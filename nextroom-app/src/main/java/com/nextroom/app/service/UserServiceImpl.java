package com.nextroom.app.service;

import com.nextroom.app.dto.UserRequestDTO;
import com.nextroom.app.model.User;
import com.nextroom.app.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void saveUser(UserRequestDTO userRequestDTO) {
        // Check if user already exists by email
        Optional<User> existingUser = userRepository.findByEmail(userRequestDTO.getEmail());
        if (existingUser.isPresent()) {
            //throw new UserAlreadyExistsException(USER_ALREADY_EXISTS);
        }

        // Map the DTO to User entity and save
        User user = modelMapper.map(userRequestDTO, User.class);
        user.setStatus(true);
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
}
