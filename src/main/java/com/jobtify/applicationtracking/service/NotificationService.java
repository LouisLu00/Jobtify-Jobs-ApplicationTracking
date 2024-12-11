package com.jobtify.applicationtracking.service;

import com.jobtify.applicationtracking.event.ApplicationCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */

@Service
public class NotificationService {
    private final RestTemplate restTemplate;

    @Value("${MQ.send.url}")
    private String sendUrl;

    public NotificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @EventListener
    public void handleApplicationCreated(ApplicationCreatedEvent event) {
        Long userId = event.getApplication().getUserId();
        Long jobId = event.getApplication().getJobId();

        String notificationServiceUrl = sendUrl + "/send";
        String notificationBody = String.format("User %s applied for Job %s", userId, jobId);

        try {
            restTemplate.postForEntity(notificationServiceUrl, Map.of("message", notificationBody), Void.class);
            System.out.println("Notification sent for Application ID: " + event.getApplication().getApplicationId());
        } catch (Exception e) {
            System.err.println("Failed to send notification for Application ID: " + event.getApplication().getApplicationId());
        }
    }
}
