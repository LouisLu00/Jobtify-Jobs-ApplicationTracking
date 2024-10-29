package com.jobtify.applicationtracking.service;

import com.jobtify.applicationtracking.model.Application;
import com.jobtify.applicationtracking.model.UserApplied;
import com.jobtify.applicationtracking.repository.ApplicationRepository;
import com.jobtify.applicationtracking.repository.UserAppliedRepository;
import com.jobtify.applicationtracking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */

@Service
public class ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserAppliedRepository userAppliedRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Application> getApplicationsByUserId(Long userId) {
        return applicationRepository.findByUserId(userId);
    }

    public Long getJobIdByApplicationId(Long applicationId) {
        return applicationRepository.findJobIdByApplicationId(applicationId);
    }

    public Application createApplication(Long userId, Application application) {
        Application savedApplication = applicationRepository.save(application);

        UserApplied userApplied = new UserApplied();
        UserApplied.UserAppliedKey userAppliedKey = new UserApplied.UserAppliedKey(userId, savedApplication.getApplicationId());

        userApplied.setId(userAppliedKey);
        userApplied.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
        userApplied.setApplication(savedApplication);

        userAppliedRepository.save(userApplied);

        return savedApplication;
    }

    public Application updateApplicationStatus(Long applicationId, String status) {
        Application application = applicationRepository.findById(applicationId).orElseThrow(() -> new RuntimeException("Application Not found"));
        application.setApplicationStatus(status);
        return applicationRepository.save(application);
    }
}
