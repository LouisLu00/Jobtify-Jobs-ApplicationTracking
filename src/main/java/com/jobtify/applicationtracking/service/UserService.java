package com.jobtify.applicationtracking.service;

import com.jobtify.applicationtracking.model.User;
import com.jobtify.applicationtracking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User getUserByEmailAndPassword(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }
}
