package com.jobtify.applicationtracking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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

    public String validateUser(Long userId) {
        String userUrl = userServiceUrl + "/" + userId + "/exists";
        try {
            restTemplate.getForObject(userUrl, Boolean.class);
            return null;
        } catch (HttpClientErrorException e) {
            return handleNotFoundOrServerError(e, "User");
        }
    }


    public String validateJob(Long jobId) {
        String jobUrl = jobServiceUrl + "/" + jobId;
        try {
            restTemplate.getForObject(jobUrl, Object.class);
            return null;
        } catch (HttpClientErrorException e) {
            return handleNotFoundOrServerError(e, "Job");
        }
    }

    private String handleNotFoundOrServerError(HttpClientErrorException e, String entityType) {
        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
            System.err.println(entityType + " not found.");
            return entityType + " not found";
        } else if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
            throw new RuntimeException(entityType + " service returned 500 error");
        }
        throw new RuntimeException("Error validating " + entityType + " ID");
    }
}
