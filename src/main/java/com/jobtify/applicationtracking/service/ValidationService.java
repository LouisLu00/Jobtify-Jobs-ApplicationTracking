package com.jobtify.applicationtracking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
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
        String userUrl = userServiceUrl + "/" + userId;
        try {
            restTemplate.getForObject(userUrl, Object.class);
        } catch (Exception e) {
            handleHttpClientError(e, "User");
        }
    }

    public void validateJob(Long jobId) {
        String jobUrl = jobServiceUrl + "/" + jobId;
        try {
            restTemplate.getForObject(jobUrl, Object.class);
        } catch (Exception e) {
            handleHttpClientError(e, "Job");
        }
    }

    private void handleHttpClientError(Exception e, String entityType) {
        if (e instanceof HttpClientErrorException httpException) {
            if (httpException.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, entityType + " not found");
            } else if (httpException.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, entityType + " server error");
            }
        } else if (e instanceof ResourceAccessException) {
            if (e.getCause() instanceof java.net.SocketTimeoutException) {
                throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, entityType + " service timeout", e);
            } else {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, entityType + " service unavailable", e);
            }
        }

        throw new RuntimeException("Unexpected error validating " + entityType + " ID", e);
    }
}
