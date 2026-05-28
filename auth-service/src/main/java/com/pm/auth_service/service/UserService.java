package com.pm.auth_service.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pm.auth_service.model.User;
import com.pm.auth_service.repository.UserRespository;

@Service
public class UserService {

    private final UserRespository userRepository;

    public UserService(UserRespository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByEmail(String email) {
        // Implement logic to find user by email from the database
        return userRepository.findByEmail(email);
    }

}
