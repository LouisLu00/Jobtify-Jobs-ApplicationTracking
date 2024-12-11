package com.jobtify.applicationtracking.service;

import com.jobtify.applicationtracking.model.Application;
import com.jobtify.applicationtracking.repository.ApplicationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final WebClient.Builder webClientBuilder;

    private static final Set<String> VALID_STATUSES = Set.of(
            "saved", "applied", "withdraw", "offered", "rejected", "interviewing", "archived", "screening"
    );

    @Value("${job.service.url}")
    private String jobServiceUrl;

    // Insert by Constructor
    public ApplicationService(ApplicationRepository applicationRepository,
                              WebClient.Builder webClientBuilder) {
        this.applicationRepository = applicationRepository;
        this.webClientBuilder = webClientBuilder;
    }

    // POST: Create new application
    public Application createApplication(Long userId, Long jobId, Application application) {
        if (!VALID_STATUSES.contains(application.getApplicationStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid application status: " + application.getApplicationStatus());
        }

        application.setUserId(userId);
        application.setJobId(jobId);

        Application createdApplication = applicationRepository.save(application);
        incrementJobApplicantCountAsync(jobId);
        return createdApplication;
    }

    // PUT: update application
    public Application updateApplication(Long applicationId, String status, String notes, LocalDateTime timeOfApplication) {
        if (!VALID_STATUSES.contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid application status: " + status);
        }

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
        if (status != null) application.setApplicationStatus(status);
        if (notes != null) application.setNotes(notes);
        if (timeOfApplication != null) application.setTimeOfApplication(timeOfApplication);
        return applicationRepository.save(application);
    }

    // GET: get all application by user_id
    public List<Application> getApplicationsByUserId(Long userId, String status) {
        List<Application> applications = (status != null)
                ? applicationRepository.findByUserIdAndApplicationStatus(userId, status)
                : applicationRepository.findByUserId(userId);

        if (applications.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No applications found for user ID: " + userId);
        }
        return applications;
    }

    // GET: get all application by job_id
    public List<Application> getApplicationsByJobId(Long jobId, String status) {
        List<Application> applications = (status != null)
                ? applicationRepository.findByJobIdAndApplicationStatus(jobId, status)
                : applicationRepository.findByJobId(jobId);

        if (applications.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No applications found for job ID: " + jobId);
        }
        return applications;
    }

    // GET: get application by application_id
    public List<Application> getApplicationByApplicationId(Long applicationId) {
        List<Application> applications = applicationRepository.findByApplicationId(applicationId);
        if (applications.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No application found with ID: " + applicationId);
        }
        return applications;
    }

    // Delete an application
    public void deleteApplication(Long applicationId) {
        if (!applicationRepository.existsById(applicationId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found");
        }
        applicationRepository.deleteById(applicationId);
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
}
