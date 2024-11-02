package com.jobtify.applicationtracking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */
@Service
public class ValidationService {
    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${job.service.url}")
    private String jobServiceUrl;

    private final RestTemplate restTemplate;

    public ValidationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void validateUser(Long userId) {
        String userUrl = userServiceUrl + "/" + userId + "/exists";
        try {
            restTemplate.getForObject(userUrl, Boolean.class);
        } catch (HttpClientErrorException e) {
            handleNotFoundOrServerError(e, "User");
        }
    }

    public void validateJob(Long jobId) {
        String jobUrl = jobServiceUrl + "/" + jobId;
        try {
            restTemplate.getForObject(jobUrl, Object.class);
        } catch (HttpClientErrorException e) {
            handleNotFoundOrServerError(e, "Job");
        }
    }

    private void handleNotFoundOrServerError(HttpClientErrorException e, String entityType) {
        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, entityType + " not found");
        } else if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, entityType + " server error");
        }
        throw new RuntimeException("Error validating " + entityType + " ID");
    }
}
