package com.jobtify.applicationtracking.service;

import com.jobtify.applicationtracking.model.Application;
import com.jobtify.applicationtracking.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    // Insert by Constructor
    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    // POST: Create new application
    public Application createApplication(Long userId, Long jobId, Application application) {
        application.setUserId(userId);
        application.setJobId(jobId);
        return applicationRepository.save(application);
    }

    // PUT: update application
    public Application updateApplication(Long applicationId, String status, String notes, LocalDateTime timeOfApplication) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        if (status != null) application.setApplicationStatus(status);
        if (notes != null) application.setNotes(notes);
        if (timeOfApplication != null) application.setTimeOfApplication(timeOfApplication);
        return applicationRepository.save(application);
    }

    // GET: get all application by user_id
    public List<Application> getApplicationsByUserId(Long userId, String status) {
        if (status != null) {
            return applicationRepository.findByUserIdAndApplicationStatus(userId, status);
        }
        return applicationRepository.findByUserId(userId);
    }

    // GET: get all application by job_id
    public List<Application> getApplicationsByJobId(Long jobId, String status) {
        if (status != null) {
            return applicationRepository.findByJobIdAndApplicationStatus(jobId, status);
        }
        return applicationRepository.findByJobId(jobId);
    }

    // Delete an application
    public void deleteApplication(Long applicationId) {
        if (!applicationRepository.existsById(applicationId)) {
            throw new RuntimeException("Application not found");
        }
        applicationRepository.deleteById(applicationId);
    }
}
