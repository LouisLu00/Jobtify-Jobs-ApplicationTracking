package com.jobtify.applicationtracking.service;

import com.jobtify.applicationtracking.model.Application;
import com.jobtify.applicationtracking.repository.ApplicationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final RestTemplate restTemplate;
    private final WebClient.Builder webClientBuilder;

    // Insert by Constructor
    public ApplicationService(ApplicationRepository applicationRepository, RestTemplate restTemplate, WebClient.Builder webClientBuilder) {
        this.applicationRepository = applicationRepository;
        this.restTemplate = restTemplate;
        this.webClientBuilder = webClientBuilder;
    }

    // POST: Create new application
    public Application createApplication(Long userId, Long jobId, Application application) {
//
//        CompletableFuture<Boolean> jobValidationFuture = validateJobAsync(jobId);
//        Boolean jobExists;
//        try {
//            jobExists = jobValidationFuture.get();
//        } catch (Exception e) {
//            throw new RuntimeException("Error validating Job ID: " + jobId, e);
//        }
//        if (!jobExists) {
//            throw new RuntimeException("Job with ID " + jobId + " not found.");
//        }

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

    public boolean validateUser(Long userId) {
        String userUrl = "http://3.16.10.86:8080/api/users/" + userId + "/exists";
        try {
            Boolean userExists = restTemplate.getForObject(userUrl, Boolean.class);
            return userExists != null && userExists;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw new RuntimeException("Error validating User ID: " + userId, e);
        }
    }

}
