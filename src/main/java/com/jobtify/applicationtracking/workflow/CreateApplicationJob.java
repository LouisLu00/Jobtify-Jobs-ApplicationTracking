package com.jobtify.applicationtracking.workflow;

import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */
@Component
public class CreateApplicationJob implements Job {
    @Value("${job.service.url}")
    private String jobServiceUrl;

    @Value("${MQ.service.url}")
    private String mqServiceUrl;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            WebClient.Builder webClientBuilder = SpringContext.getBean(WebClient.Builder.class);
            RestTemplate restTemplate = SpringContext.getBean(RestTemplate.class);

            Long jobId = context.getJobDetail().getJobDataMap().getLong("jobId");
            Long userId = context.getJobDetail().getJobDataMap().getLong("userId");

            System.out.println("Executing Quartz Job: Publish and increment applicant count.");

            incrementJobApplicantCountAsync(webClientBuilder, jobId);
            publish(restTemplate, jobId, userId);

            System.out.println("Job completed: Publish and increment applicant count for Job ID " + jobId);

        } catch (Exception e) {
            throw new JobExecutionException("Failed to execute CreateApplicationJob", e);
        }
    }

    private void incrementJobApplicantCountAsync(WebClient.Builder webClientBuilder, Long jobId) {
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

    private void publish(RestTemplate restTemplate, Long jobId, Long userId) {
        JSONObject messageBodyJson = new JSONObject();
        messageBodyJson.put("jobId", jobId);
        messageBodyJson.put("userId", userId);

        String messageBody = messageBodyJson.toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(messageBody, headers);

        String queueServiceUrl = mqServiceUrl + "/publish";
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
    }
}
