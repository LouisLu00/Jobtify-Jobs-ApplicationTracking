package com.jobtify.applicationtracking.controller;

import com.jobtify.applicationtracking.model.Application;
import com.jobtify.applicationtracking.model.User;
import com.jobtify.applicationtracking.service.ApplicationService;
import com.jobtify.applicationtracking.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationService applicationService;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginRequest request) {
        User user = userService.getUserByEmailAndPassword(request.getEmail(), request.getPassword());
        if (user != null) {
            List<Application> applications = applicationService.getApplicationsByUserId(user.getUserId());
            return ResponseEntity.ok(new UserLoginResponse(user, applications));
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    // DTO for login request
    @Data
    public static class UserLoginRequest {
        private String email;
        private String password;

    }

    // DTO for login response
    @Data
    public static class UserLoginResponse {
        private User user;
        private List<Application> applications;

        public UserLoginResponse(User user, List<Application> applications) {
            this.user = user;
            this.applications = applications;
        }

    }
}
