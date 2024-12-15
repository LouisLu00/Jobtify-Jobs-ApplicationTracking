package com.jobtify.applicationtracking.service;

import com.jobtify.applicationtracking.model.Application;
import com.jobtify.applicationtracking.repository.ApplicationRepository;
import com.jobtify.applicationtracking.workflow.CreateApplicationJob;
import org.quartz.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    private final Scheduler scheduler;

    private static final Set<String> VALID_STATUSES = Set.of(
            "saved", "applied", "withdraw", "offered", "rejected", "interviewing", "archived", "screening"
    );

    // Insert by Constructor
    public ApplicationService(ApplicationRepository applicationRepository, Scheduler scheduler) {
        this.applicationRepository = applicationRepository;

        this.scheduler = scheduler;
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
        scheduleCreateApplicationJob(userId, jobId);

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

    // GET: get statistics by user_id
    public Map<String, Object> getApplicationsGroupedByStatusAndMonth(Long userId) {
        List<Application> applications = applicationRepository.findByUserId(userId);
        if (applications.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No applications found for user ID: " + userId);
        }

        Map<String, Long> statusCounts = applications.stream()
                .collect(Collectors.groupingBy(Application::getApplicationStatus, Collectors.counting()));

        Map<Integer, Map<String, Long>> dateCounts = applications.stream()
                .filter(app -> app.getTimeOfApplication() != null)
                .collect(Collectors.groupingBy(
                        app -> app.getTimeOfApplication().getYear(),
                        Collectors.groupingBy(
                                app -> app.getTimeOfApplication().getMonth().name(),
                                Collectors.counting()
                        )
                ));

        Map<String, Object> result = new HashMap<>();
        result.put("status", statusCounts);
        result.put("date", dateCounts);
        return result;
    }

    // Delete an application
    public void deleteApplication(Long applicationId) {
        if (!applicationRepository.existsById(applicationId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found");
        }
        applicationRepository.deleteById(applicationId);
    }

    private void scheduleCreateApplicationJob(Long userId, Long jobId) {
        try {
            String jobIdentity = "createApplicationJob_" + userId + "_" + jobId + "_" + System.currentTimeMillis();
            String triggerIdentity = "trigger_createApplication_" + userId + "_" + jobId + "_" + System.currentTimeMillis();

            JobKey jobKey = new JobKey(jobIdentity);
            TriggerKey triggerKey = new TriggerKey(triggerIdentity);

            JobDetail jobDetail = JobBuilder.newJob(CreateApplicationJob.class)
                    .withIdentity(jobKey)
                    .usingJobData("userId", userId)
                    .usingJobData("jobId", jobId)
                    .storeDurably()
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .forJob(jobDetail)
                    .withIdentity(triggerKey)
                    .startNow()
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            System.out.println("Job scheduled successfully: " + jobKey);

        } catch (SchedulerException e) {
            e.printStackTrace();
            System.err.println("Failed to schedule Quartz job: " + e.getMessage());
        }
    }
}
