package com.jobtify.applicationtracking.service;

import com.jobtify.applicationtracking.model.Application;
import com.jobtify.applicationtracking.repository.ApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ValidationService validationService;
    private final WebClient.Builder webClientBuilder;

    @Value("${job.service.url}")
    private String jobServiceUrl;

    // Insert by Constructor
    public ApplicationService(ApplicationRepository applicationRepository,
                              ValidationService validationService,
                              WebClient.Builder webClientBuilder) {
        this.applicationRepository = applicationRepository;
        this.validationService = validationService;
        this.webClientBuilder = webClientBuilder;
    }

    // POST: Create new application
    public Application createApplication(Long userId, Long jobId, Application application) {
        String userError = validationService.validateUser(userId);
        String jobError = validationService.validateJob(jobId);

        if (userError != null) {
            throw new RuntimeException(userError);
        }
        if (jobError != null) {
            throw new RuntimeException(jobError);
        }

        validateApplicationFields(application);

        application.setUserId(userId);
        application.setJobId(jobId);

        Application createdApplication = applicationRepository.save(application);
        incrementJobApplicantCountAsync(jobId);
        return createdApplication;
    }

    // PUT: update application
    public Application updateApplication(Long applicationId, String status, String notes, LocalDateTime timeOfApplication) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application with ID " + applicationId + " not found"));
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
    public boolean deleteApplication(Long applicationId) {
        if (!applicationRepository.existsById(applicationId)) {
            return false;
        }
        applicationRepository.deleteById(applicationId);
        return true;
    }

    private void incrementJobApplicantCountAsync(Long jobId) {
        String jobUrl = jobServiceUrl + "/async/update/" + jobId;

        webClientBuilder.build()
                .post()
                .uri(jobUrl)
                .retrieve()
                .toBodilessEntity()
                .toFuture()
                .thenAccept(response -> {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        System.out.println("Applicant count increment accepted for Job ID: " + jobId);
                    } else {
                        System.out.println("Failed to increment applicant count for Job ID: " + jobId);
                    }
                })
                .exceptionally(ex -> {
                    System.err.println("Error incrementing applicant count for Job ID: " + jobId + ". Error: " + ex.getMessage());
                    return null;
                });
    }

    private void validateApplicationFields(Application application) {
        StringBuilder missingFields = new StringBuilder();

        if (application.getApplicationStatus() == null || application.getApplicationStatus().isEmpty()) {
            missingFields.append("applicationStatus, ");
        }

        if (application.getTimeOfApplication() == null) {
            missingFields.append("timeOfApplication, ");
        }

        if (application.getNotes() == null) {
            application.setNotes("");
        }

        if (!missingFields.isEmpty()) {
            missingFields.setLength(missingFields.length() - 2);
            throw new IllegalArgumentException("Missing required fields: " + missingFields);
        }
    }
}
