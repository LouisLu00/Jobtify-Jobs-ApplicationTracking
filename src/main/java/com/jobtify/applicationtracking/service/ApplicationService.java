package com.jobtify.applicationtracking.service;

import com.jobtify.applicationtracking.model.Application;
import com.jobtify.applicationtracking.repository.ApplicationRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.json.JSONObject;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final WebClient.Builder webClientBuilder;

    private final RestTemplate restTemplate;


    private static final Set<String> VALID_STATUSES = Set.of(
            "saved", "applied", "withdraw", "offered", "rejected", "interviewing", "archived", "screening"
    );

    @Value("${job.service.url}")
    private String jobServiceUrl;

    @Value("${MQ.service.url}")
    private String mqServiceUrl;

    // Insert by Constructor
    public ApplicationService(ApplicationRepository applicationRepository,
                              WebClient.Builder webClientBuilder,
                              RestTemplate restTemplate) {
        this.applicationRepository = applicationRepository;
        this.webClientBuilder = webClientBuilder;
        this.restTemplate = restTemplate;
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

        JSONObject messageBodyJson = new JSONObject();
        messageBodyJson.put("jobId", jobId);
        messageBodyJson.put("userId", userId);

        String messageBody = messageBodyJson.toString(); // Convert to string for messageBody
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(messageBody.toString(), headers);


        String queueServiceUrl = mqServiceUrl +"/publish";
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    queueServiceUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                String result = responseEntity.getBody();
                System.out.println("Response from queue service: " + result);
            } else if (responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST) {
                System.err.println("Validation error: " + responseEntity.getBody());
            } else {
                System.err.println("Failed to send message. HTTP Status: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error while sending message: " + e.getMessage());
            e.printStackTrace();
        }

        // Continue with application processing
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

    // GET: get application by application_id
    public List<Application> getApplicationByApplicationId(Long applicationId) {
        return applicationRepository.findByApplicationId(applicationId);

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
